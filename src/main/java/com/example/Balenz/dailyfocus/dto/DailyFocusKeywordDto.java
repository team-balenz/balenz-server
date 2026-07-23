package com.example.Balenz.dailyfocus.dto;

import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.Category;

import java.util.List;

public record DailyFocusKeywordDto(
        Long id,
        String name,
        String imageUrl,
        Category category,
        ScopeSectionResponseDto.ArticleCountDto articleCount,
        List<DailyFocusArticleDto> articles,
        boolean scraped
) {
}
