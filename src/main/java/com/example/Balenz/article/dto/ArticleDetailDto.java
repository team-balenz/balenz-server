package com.example.Balenz.article.dto;

import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonPropertyOrder({
        "title",
        "newsAgencyName",
        "publishedAt",
        "frameType",
        "summary",
        "articleUrl"
})
public class ArticleDetailDto {

    private String title;

    private String newsAgencyName;

    private LocalDate publishedAt;

    private FrameType frameType;

    private String summary;

    private String articleUrl;

    private List<RelatedArticleDto> allRelatedArticles;

    private List<RelatedArticleDto> valueRelatedArticles;

    private List<RelatedArticleDto> neutralRelatedArticles;

    private List<RelatedArticleDto> normRelatedArticles;

    private List<ScopeSectionResponseDto.KeywordDto> hotKeywords;

}
