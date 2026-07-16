package com.example.Balenz.dailyfocus.dto;

import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;

import java.util.List;

public record DailyFocusCacheDto(
        Long id,
        String name,
        String imageUrl,
        ScopeSectionResponseDto.ArticleCountDto articleCount,
        List<DailyFocusArticleDto> articles
) {
}
