package com.example.Balenz.search.dto;

import com.example.Balenz.article.dto.SimpleArticleWithoutImageDto;
import com.example.Balenz.keyword.dto.KeywordDto;

import java.util.List;

public record SearchResponseDto(
        List<KeywordDto> keywords,
        List<SimpleArticleWithoutImageDto> articles
) {
}
