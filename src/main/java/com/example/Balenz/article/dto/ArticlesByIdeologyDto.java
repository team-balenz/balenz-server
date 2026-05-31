package com.example.Balenz.article.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArticlesByIdeologyDto {

    private List<SimpleArticleWithoutImageDto> value;

    private List<SimpleArticleWithoutImageDto> neutral;

    private List<SimpleArticleWithoutImageDto> norm;

}
