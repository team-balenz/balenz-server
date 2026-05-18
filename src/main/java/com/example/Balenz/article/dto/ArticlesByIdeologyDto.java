package com.example.Balenz.article.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArticlesByIdeologyDto {

    private List<SimpleArticleDto> value;

    private List<SimpleArticleDto> neutral;

    private List<SimpleArticleDto> norm;

}
