package com.example.Balenz.keyword.dto;

import com.example.Balenz.keyword.entity.DominantFrameType;
import lombok.*;

import java.util.List;

@Data
@Builder
public class ScopeSectionResponseDto {

    private List<MainKeywordDto> mainKeywords;

    private List<KeywordDto> keywords;

    @Data
    @Builder
    public static class MainKeywordDto {

        private String category;

        private Long id;

        private String name;

        private String valueImageUrl;

        private String valueArticleTitle;

        private String normImageUrl;

        private String normArticleTitle;

        private ArticleCountDto articleCount;

    }

    @Data
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleCountDto {

        private Long value;

        private Long neutral;

        private Long norm;

        private Double valueRatio;

        private Double neutralRatio;

        private Double normRatio;

    }

}
