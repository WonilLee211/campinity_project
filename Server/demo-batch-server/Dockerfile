# 서버를 구동시킬 자바를 받아옵니다.
FROM openjdk:11 as build

# `JAR_FILE` 이라는 이름으로 build 한 jar 파일을 지정합니다.
ARG JAR_FILE=./build/libs/*.jar

# 지정한 jar 파일을 batch.jar 라는 이름으로 Docker Container에 추가합니다.
ADD ${JAR_FILE} batch.jar

# batch.jar 파일을 실행합니다.
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","/batch.jar"]
