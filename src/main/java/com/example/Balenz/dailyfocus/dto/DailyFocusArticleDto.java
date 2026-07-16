package com.example.Balenz.dailyfocus.dto;

import com.example.Balenz.article.entity.FrameType;

public record DailyFocusArticleDto(
        Long id,
        String title,
        String newsAgencyName,
        FrameType frameType,
        String summary
) {
}
