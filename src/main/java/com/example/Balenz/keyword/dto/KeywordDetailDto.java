package com.example.Balenz.keyword.dto;

import com.example.Balenz.article.dto.RelatedArticlesDto;
import com.example.Balenz.keyword.entity.DominantFrameType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonPropertyOrder({ // 키워드 정보가 먼저 반환되도록 순서 유지
        "id", "name", "imageUrl", "date", "viewCount", "keywordSummary",
        "articleCount", "bias", "dominantFrameType", "mainArticles"
}
)
public class KeywordDetailDto {

    private Long id;

    private String name;

    private String imageUrl;

    private LocalDate date;

    private Long viewCount;

    private String keywordSummary;

    private ScopeSectionResponseDto.ArticleCountDto articleCount;

    private int bias;

    private DominantFrameType dominantFrameType;

    private MainArticlesDto mainArticles;

    private RelatedArticlesDto relatedArticles;

    private List<ScopeSectionResponseDto.KeywordDto> hotKeywords;

    @Data
    @Builder
    public static class MainArticlesDto {

        RelatedArticlesDto.RelatedArticleDto value;

        RelatedArticlesDto.RelatedArticleDto neutral;

        RelatedArticlesDto.RelatedArticleDto norm;

    }

}
