# Readme

## 🏕 Campinity?

<aside>
💡 Campinity 프로젝트는 캠핑에 관한 다양한 정보와 실시간 소통을 제공하는 안드로이드 앱 서비스입니다. 실시간으로 캠핑장 안/밖으로 소통할 방법이 없고 캠핑장 정보를 찾기 위해 여러 플랫폼을 검색해야 한다는 점을 Pain Point로 삼고 이를 해결할 서비스를 개발하고자 했습니다. 정제된 정보와 실시간 소통을 통해 캠퍼들이 편리하게 캠핑을 할 수 있도록 서비스를 구현했습니다.

</aside>


---

## 👨‍👦‍👦 **BackEnd로서 제가 수행한 역할**입니다.

담당 업무

- 요구사항 분석에 따른 ERD 및 API 설계
    - 1:N, N:M 관계, 양방향 단방향 관계 최적화
- gocamp 외부 API 파싱을 통한 데이터 내재화
- 테스트 주도 API 기능 개발
- Firebase Cloud messaging을 이용한 캠핑장 푸시 알람 구독 서비스 로직 구현
    - 에러 발생 및 토큰 값 변경 시 기존 아이템 삭제를 통한 fcm token 신선도 보수적 관리
- Batch를 통한 누적 데이터 관리
    - soft delete를 통한 유저 활동 이력 누적
    - 누적데이터를 이용한 실시간 인기있는 캠핑장 통계 데이터 산출
    - 통계 자료를 Redis에 저장하여 데이터 캐싱
- query dsl을 활용한 캠핑장 다중 조회 필터링
    - 9 개 항목에 대한 다중 필터링 조회 기능 구현

### backend 팀 개발 일지

[backend trouble shooting](https://www.notion.so/backend-trouble-shooting-20800659bdf24d6d80b67c69b7ddbd0d)

### 코드 리뷰 및 회고

### 1. 고민했던 부분

- **영속성 전이**
    - 서비스 로직에 맞게 데이터 변화에 따라 관계형 데이터들에 적절히 반영될 수 있도록 설정했습니다.
    
    ```java
    ...
    public class LikeMessage extends BaseEntity {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
    
        @ManyToOne(cascade = CascadeType.MERGE)
        @ToString.Exclude
        private Message message;
    
        @ManyToOne(cascade = CascadeType.MERGE)
        @ToString.Exclude
        private Member member;
    
        ...
    }
    ```
    
- **frontend와의 소통**
    - 여러 예외상황에 대한 분기처리가 가능하도록 에외 메세지를 커스텀했습니다.
        
        ```java
        @Getter
        public enum ErrorMessageEnum {
            CAMPSITE_NOT_FOUND("캠핑장을 찾을 수 없습니다."),
            ...
            private String message;
        
            ErrorMessageEnum(String message) {
                this.message = message;
            }
        }
        ```
        
    - SWAGGER API 문서화 라이브러리를 사용함으로써 문서 기반 소통이 원활하도록 했습니다.
        
        ```java
        @Api(tags = "FCM 관련 API")
        @RestController
        @RequiredArgsConstructor
        @RequestMapping("/api/v9/fcm")
        public class FcmController {
        		...
        
            @ApiResponses({
                    @ApiResponse(code = 200, message = "fcm token 저장 성공 시 응답"),
                    @ApiResponse(code = 400, message = "유저가 존재하지 않을 경우 응답"),
            })
            @ApiOperation(value = "fcm 저장 및 갱신 api")
            @PostMapping("/token")
            public ResponseEntity<FcmTokenResDTO> saveFcmToken(
                    @AuthenticationPrincipal MemberDetails memberDetails,
                    @RequestBody FcmTokenReqDTO fcmTokenReqDTO) {
                FcmTokenResDTO fcmTokenResDTO = fcmTokenService
                        .saveFcmToken(memberDetails.getMember().getId(), fcmTokenReqDTO.getFcmToken());
                return ResponseEntity.ok().body(fcmTokenResDTO);
            }
        		...
        }
        ```
        
- **코드 중복 최소화**
    - GlobalExceptionHandler를 적용하여 예외 처리 코드의 중복을 최소화했습니다.
    
    ```java
    @RestControllerAdvice
    public class GlobalExceptionHandler {
    
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorMessage> handle(BadRequestException e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    	 ...
    
    }
    ```
    
    - 엔터티별 공통 필드를 EntityListener를 활용하여 코드 중복을 줄였습니다.
    
    ```java
    /**
     * BaseEntity
     * @MappedSuperclass : JPA Entity 클래스들이 해당 추상클래스를 상속할 경우 내부 필드를 컬럼으로 인식
     * @EntityListeners(value = AuditingEntityListener.class)
     *      : 해당 클래스에 Auditing 기능 추가
     *      : Entity lifeCycle과 관련된 이벤트 listen 기능 추가
     */
    
    @Data
    @MappedSuperclass
    @EntityListeners(value = AuditingEntityListener.class)
    public class BaseEntity implements Auditable {
    
        @CreatedDate
        private LocalDateTime createdAt;
    
        @LastModifiedDate
        private LocalDateTime updatedAt;
    }
    ```
    
- **보안**
    - url에 PK값이 노출되지 않도록 UUID를 도입했습니다.

### 2. 아쉬웠던 부분

- 기능 개발 과정에서 Test code로 기능 안정성을 검증했지만 Worst case에 대한 세세한 검증하지 않았던 아쉬움이 남았습니다.
- 사용했던 MariaDB는 클러스터 인덱스 조회에 최적화되어 있어, UUID를 도입함으로써 조회 성능 저하가 불가피했습니다. 이에 클라이언트와의 통신에 담길 데이터만 UUID로 사용하고, 내부적으로 pk값으로 조회하도록 로직을 설계했습니다. 또한, 클라이언트에서 직접 조회하지 않는 참조 테이블에 대해서 UUID를 적용하지 않는 등 최적화를 고민했습니다.
- Fcm Messaging 서비스를 혼자서 구현하다보니 제가 생각했던 데이터 관리 방식과 클라이언트에서 구현한 요청 방식이 달라 불필요한 데이터가 데이터베이스에 쌓이는 문제가 있었습니다.
    - 또한 부하테스트를 진행하지 못했지만 조회와 생성, 삭제, fcm과 통신 등 여러 작업이 한꺼번에 발생하며 서비스 이용 빈도가 높은 기능이기에 block IO에서 요청 실패가 발생할 가능성이 높았습니다.

### 3. 개선해야할 점

- Worst case까지 검증하는 test code를 작성하여 기능 안정성 확보하기
- 복잡한 서비스 로직의 경우 바쁘더라도 클라이언트와 지속적으로 소통하고 각자의 생각을 명확히 전달하기
- 부하테스트를 통해 실패포인트를 찾고 non block IO 프로그래밍을 적용해보고 싶음. 다음 프로젝트에 꼭 적용해보기. 파티셔닝, 샤닝, 메세지 큐 등 부하과정에서 작업 실패 케이스 최소화해보기
- 배포 과정에서 컨테이너별 접근 허용 url 설정과 계정 권한 부여를 통해 보안 이슈 대비하기

---

## 개발에 기여한 화면


- 초기 화면

<img src="./readme-images/Screenshot_20230217_104621_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_104628_CAMPINITY.jpg" width="200" height="400"/><br/>


- 캠핑장 등록 후 (구독, 클러스터링)

<img src="./readme-images/Screenshot_20230217_104751_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_104755_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_174713_CAMPINITY.jpg" width="200" height="400"/><br/>

- 우체통 (목록, 상세)

<img src="./readme-images/Screenshot_20230217_104820_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_104824_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_104840_CAMPINITY.jpg" width="200" height="400"/><br/>

- 우체통 (질문, 답변)

<img src="./readme-images/Screenshot_20230217_104832_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_104844_CAMPINITY.jpg" width="200" height="400"/><br/>

- 위치 추적 모드

<img src="./readme-images/Screenshot_20230217_104932_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_104939_CAMPINITY.jpg" width="200" height="400"/><br/>

- 쪽지 작성 (리뷰 쪽지)

<img src="./readme-images/Screenshot_20230217_104945_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_105034_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_105030_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_105243_CAMPINITY.jpg" width="200" height="400"/><br/>

- 쪽지 작성 (자유 쪽지)
 
<img src="./readme-images/Screenshot_20230217_105056_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_104909_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_104905_CAMPINITY.jpg" width="200" height="400"/><br/>

- 쪽지 작성 (도움 주기)

<img src="./readme-images/Screenshot_20230217_172318_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217-093224_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217-111528_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217-092058_CAMPINITY.jpg" width="200" height="400"/>


### 🗺 마이페이지


<img src="./readme-images/Screenshot_20230217_112739_CAMPINITY.jpg" width="200" height="400"/> <img src="./readme-images/Screenshot_20230217_112745_CAMPINITY.jpg" width="200" height="400"/>\ <img src="./readme-images/Screenshot_20230217_112828_CAMPINITY.jpg" width="200" height="400"/></br>


### 📌프로젝트 진행기간

- 2023.01.03 ~ 2012.02.17
- SSAFY 8기 2학기 공통 프로젝트 - Campinity

### 📌Campinity 기획 배경

<img src="./readme-images/KakaoTalk_20230217_121235438.png" width="300" /> <img src="./readme-images/KakaoTalk_20230217_121235596.png" width="300" /> <img src="./readme-images/KakaoTalk_20230217_121235777.png" width="500" /></br>

캠핑 산업은 지속적으로 커져 감에 반해서 현재 캠핑장과 관련된 자료를 얻을 수 있는 앱과 카페는 거의 없는 편입니다. 있다고 하더라도 보통 예약과 정보 전달에 치중된 경우가 대부분이고, 정보들 역시 여러 플랫폼으로 분산되어 있어서 캠핑에 관한 정보를 얻기 위해서는 여러 플랫폼을 방문해야 합니다. 보통 사용자들은 캠핑시에는 위생시설, 접근성, 사이트 편리성을 주로 고려하여 캠핑장을 선택한다고 합니다. 그러나 위생시설이나 접근성 같은 정보들은 현재의 플랫폼들에서 얻기에는 무리가 있습니다.

이런 문제점들을 해결하기 위해서 저희는 1. 분산된 정보를 하나의 플랫폼으로 2. 캠핑장 내외 사용자들에 대한 실시간 커뮤니티 제공이라는 두가지 해결방안에 초점을 맞추어 Campinity 기획 및 개발하였습니다. 

## 📃 Description

> 서비스 전체 기능
> 
1. 캠핑장 정보 조회
    - 필터/지역별 정보 조회
    - 지도/리스트 두 가지 형식으로 조회 가능
    - 캠핑장 리뷰 작성/조회
    - 실시간 캠핑장별 질문 우체통 기능
    - 해당 캠핑장에 위치한 사람들이 작성한 위/경도별 리뷰쪽지 확인
    - 정보 클러스터링
2. 커뮤니티
    - 현재 위치한 캠핑장 위치 설정
    - 같은 캠핑장에 위치한 사람들만 볼 수 있는 자유쪽지 작성
    - 리뷰쪽지 작성
    - 지도 기반으로 마커로 등록된 쪽지 확인 기능
    - FCM을 통한 푸시알림
    - 웹소켓을 통한 실시간 채팅
3. 큐레이션
    - 카테고리별 엄선된 캠핑 관련 정보 제공
4. 컬렉션
    - 사용자가 여행 추억을 남길 수 있도록 사진, 날짜 등 간단한 여행 정보를 담은 포토카드 기능 제공
    - 사용자의 편의성을 위해 2가지 뷰 모드 제공 (카드 스와이핑 뷰 & 목록 뷰)

## 📝 Design

> 전체 기술 스택
> 

![KakaoTalk_20230217_121317689.png](./readme-images/KakaoTalk_20230217_121317689.png)

> ERD 다이어그램
> 

![KakaoTalk_20230217_121349147.png](./readme-images/KakaoTalk_20230217_121349147.png)

> 요구사항 명세서
> 

![KakaoTalk_20230217_121509675.png](./readme-images/KakaoTalk_20230217_121509675.png)

![KakaoTalk_20230217_121528407.png](./readme-images/KakaoTalk_20230217_121528407.png)

![KakaoTalk_20230217_121554050.png](./readme-images/KakaoTalk_20230217_121554050.png)

![KakaoTalk_20230217_121623938.png](./readme-images/KakaoTalk_20230217_121623938.png)

![KakaoTalk_20230217_121639297.png](./readme-images/KakaoTalk_20230217_121639297.png)

![KakaoTalk_20230217_121720445.png](./readme-images/KakaoTalk_20230217_121720445.png)

> API 명세서
> 

![KakaoTalk_20230217_121808980.png](./readme-images/KakaoTalk_20230217_121808980.png)

[https://nosy-elbow-d3d.notion.site/REST-API-27368aa23a124e3588456d57a5766905](https://www.notion.so/REST-API-27368aa23a124e3588456d57a5766905)
</br>


## 💽 Back-End

> 사용한 라이브러리
> 

| 사용라이브러리 | 사용이유 |  |
| --- | --- | --- |
| aop | 서비스 입력 반환 로깅 처리 |  |
| firebase-admin | 구글 Oauth 토큰 검증, FCM 서비스 이용 |  |
| jackson-datatype-jsr310 | LocalDateTime Json 변환,반환 포맷 지정 |  |
| coomons-fileupload ,commons-io | 파일 업로드 |  |
| spring-cloud-starter-aws | aws S3서비스 사용 |  |
| jjwt-api,jjwt-impl,jjwt-jackson | 인증 인가에 사용할 jwt토큰 생성 및 검증 |  |
| spring-boot-starter-data-jpa | Mysql 에 관리,조작하기 위한 JPA API |  |
| spring-boot-starter-security | 인증,인가 특히 권한관리를 쉽게 해주는 스프링 하위 프레임워크 |  |
| springfox-swagger-ui,springfox-boot-starter | API 명세 |  |
| mysql-connector-java | DBMS |  |
| lombok | 컴파일 타임 코드 자동 생성기 |  |
| querydsl-apt, querydsl-jpa | JPA 사용 시에 복잡한 쿼리 처리에 사용 |  |
| spring-boot-starter-validation | 사용자의 입력에 대한 유효성 검사에 도움을 주는 라이브러리 |  |
| spring-boot-starter-data-redis | 휘발성 인메모리 저장소, 중복처리에 사용 |  |
| spring boot batch | 포인트 정산, 알림 발송과 같은 scheduled로 지정된 시간의 일괄처리에 사용 |  |


> 디렉토리 구조
> 

```
📦Server
 ┣ 📂demo-api-server
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂api
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂converter
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ...
 ┃ ┃ ┗ 📂test
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂demo
 ┃ ┃ ┃ ┗ 📂resources
 ┣ 📂demo-batch-server
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂demo
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂batch
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂campinityRepository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂job
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂scheduler
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂writer
 ┃ ┃ ┃ ┗ 📂resources
 ...
 ┃ ┃ ┗ 📂test
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂batch
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂JobTest
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂multiDatabase
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┗ 📂resources
 ...
 ┣ 📂demo-core
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂core
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂utils
 ┃ ┃ ┃ ┗ 📂resources
 ...
 ┃ ┃ ┗ 📂test
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂campinity
 ...
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂demo
 ┃ ┃ ┃ ┗ 📂resources
 ┣ 📂gradle
 ┃ ┗ 📂wrapper
 ┣ 📂images
 ┃ ┗ 📂message
```


> 아키텍처 구조
> 

![KakaoTalk_20230217_121249506.png](./readme-images/KakaoTalk_20230217_121249506.png)

## 👨‍👦‍👦 Team Member

> Android Part
> 
- 강은선
    - 검색 모드 화면 구현
    - 지도를 통한 검색 기능 구현
    - 검색 결과 페이지네이션 구현
    - 캠핑장 상세 페이지 화면 구현
    - 지도 마커 관련 기능 구현
    - 질문함 화면 구현
- 윤세림
    - 온보딩, 마이페이지 화면 구현
    - 소셜로그인 구현
    - 홈 화면 구현
    - 큐레이션 화면 구현
    - 컬렉션 화면 구현
    - FCM을 통한 푸시 알림 구현
    - WebSocket을 활용한 채팅 구현
- 홍민기
    - 커뮤니티 화면 구현
    - 지도를 통한 검색 기능 구현
    - 유저의 현위치를 통한 구독 기능 구현
    - 지도 마커 관련 기능 구현
    - 질문함 화면 구현

> Server Part
> 
- 권용준
    - Spring security-JWT 기반 소셜 로그인
    - Docker 및 Jenkins를 활용한 CI/CD파이프라인 구축
    - WebSocket STOMP을 활용한 채팅
    - 아키텍쳐 설계
    - 이미지 업로드 API, Service로직 구현
    - ERD 구조 설계
- 이원일
    - gocamp 외부 API 파싱을 통한 데이터 내재화
    - soft delete를 통한 아이템 삭제 로직
    - Firebase Cloud messaging 기능 구현
    - fcm token 신선도 관리 및 캠핑장 구독 관리
    - API 개발
    - fcm service - chat service 연동 비지니스 로직 구현
    - ERD 구조 설계
- 이민지
    - Batch를 통한 데이터 관리 및 통계 처리
        - soft delete, hard delete
        - multi db
        - 통계 데이터 산정
    - query dsl을 활용한 캠핑장 다중 조회 필터링 및 페이지네이션
    - 지역 데이터 구축 및 조회 클러스터링 구현
    - H2를 활용한 test 독립적인 테스트 환경 구축
    - API 개발
    - ERD 구조 설계
