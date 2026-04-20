package com.example.Balenz.keyword.dto;

import com.example.Balenz.keyword.entity.DominantFrameType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScopeSectionResponseDto {

    private MainKeywordDto mainKeyword;

    private List<KeywordDto> keywords;

    @Data
    @Builder
    public static class MainKeywordDto {

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
    public static class KeywordDto {

        private Long id;

        private String name;

        private String imageUrl;

        private ArticleCountDto articleCount;

        // 어떤 관점의 기사가 많은지
        private DominantFrameType dominantFrameType;

    }

    @Data
    @Builder
    public static class ArticleCountDto {

        private Long value;

        private Long neutral;

        private Long norm;

        private Double valueRatio;

        private Double neutralRatio;

        private Double normRatio;

    }

}
