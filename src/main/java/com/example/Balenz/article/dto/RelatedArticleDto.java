package com.example.Balenz.article.dto;

import com.example.Balenz.article.entity.FrameType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RelatedArticleDto {

    private Long id;

    private String title;

    private String newsAgencyName;

    private LocalDate publishedAt;

    private FrameType frameType;

    private String summary;

}
