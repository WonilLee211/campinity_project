package com.ssafy.campinity.core.dto;

import com.ssafy.campinity.core.entity.message.MessageCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)public class MessageReqDTO {

    @ApiModelProperty(
            value = "캠핑장 식별 아이디",
            example = "예시 68c156cf-3db2-41dd-8e4e-2e3b44d15179",
            required = true,
            dataType = "String"
    )
    private UUID campsiteId;

    @ApiModelProperty(
            value = "쪽지 타입",
            allowableValues = "리뷰,자유",
            required = true,
            dataType = "String"
    )
    private String messageCategory;


    @ApiModelProperty(
            value = "쪽지 내용",
            required = true,
            dataType = "String"
    )
    private String content;

    @ApiModelProperty(
            value = "쪽지 위도",
            required = true,
            dataType = "Double"
    )
    private double latitude;

    @ApiModelProperty(
            value = "쪽지 경도",
            required = true,
            dataType = "Double"
    )
    private double longitude;

    @ApiModelProperty(
            value = "쪽지 이미지(max-file-size: 10MB / max-request-size: 10MB)",
            dataType = "png/ jpeg"
    )
    private MultipartFile file;


    @Builder
    public MessageReqDTO(String campsiteId, String messageCategory, String content, double latitude, double longitude, MultipartFile file) {
        this.campsiteId = UUID.fromString(campsiteId);
        this.messageCategory = messageCategory;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.file = file;
    }
}
