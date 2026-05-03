package com.example.Balenz.article.dto;

import com.example.Balenz.article.entity.FrameType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleArticleDto {

    private Long id;

    private String title;

    private String newsAgencyName;

    private FrameType frameType;

}
