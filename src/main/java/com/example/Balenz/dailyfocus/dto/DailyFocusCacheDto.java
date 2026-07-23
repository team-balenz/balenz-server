package com.example.Balenz.dailyfocus.dto;

import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.Category;

import java.util.List;

public record DailyFocusCacheDto(
        Long id,
        String name,
        String imageUrl,
        ScopeSectionResponseDto.ArticleCountDto articleCount,
        List<DailyFocusArticleDto> articles,
        Category category
) {
}
