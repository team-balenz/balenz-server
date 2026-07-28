package com.example.Balenz.internal.dto;

import com.example.Balenz.article.entity.FrameType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestResponseDto {

    private String keyword;

    private String summary;

    private boolean isClassifiable;

    private FrameType frameType;

}
