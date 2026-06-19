package com.example.Balenz.article.dto;

public record IdeologyClassifyDto(
        String ideology,
        Double confidence,
        String reason
) {
}
