package com.ssafy.campinity.core.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerReqDTO {

    @ApiModelProperty(
            value = "질문 식별 아이디",
            required = true,
            dataType = "String"
    )
    private UUID questionId;

    @ApiModelProperty(
            value = "답변 내용",
            required = true,
            dataType = "String"
    )
    private String content;

    @Builder
    public AnswerReqDTO(UUID questionId, String content) {
        this.questionId = questionId;
        this.content = content;
    }
}
