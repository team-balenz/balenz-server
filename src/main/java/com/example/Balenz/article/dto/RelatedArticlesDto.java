package com.example.Balenz.article.dto;

import com.example.Balenz.article.entity.FrameType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RelatedArticlesDto {

    private List<RelatedArticleDto> all;

    private List<RelatedArticleDto> value;

    private List<RelatedArticleDto> neutral;

    private List<RelatedArticleDto> norm;

    @Data
    @Builder
    public static class RelatedArticleDto {

        private Long id;

        private String title;

        private String newsAgencyName;

        private LocalDate publishedAt;

        private FrameType frameType;

        private String summary;

    }

}
