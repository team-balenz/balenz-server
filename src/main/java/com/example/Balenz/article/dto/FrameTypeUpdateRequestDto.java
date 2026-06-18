package com.example.Balenz.article.dto;

import com.example.Balenz.article.entity.FrameType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FrameTypeUpdateRequestDto {

    @NotNull
    private Long articleId;

    @NotNull
    private FrameType frameType;

}
