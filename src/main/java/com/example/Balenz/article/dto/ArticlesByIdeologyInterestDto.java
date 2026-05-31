package com.example.Balenz.article.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArticlesByIdeologyInterestDto {

    private List<SimpleArticleWithoutImageDto> valueInterestArticles;

    private List<SimpleArticleWithoutImageDto> normInterestArticles;

}
