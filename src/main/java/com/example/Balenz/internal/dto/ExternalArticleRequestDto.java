package com.example.Balenz.internal.dto;

public record ExternalArticleRequestDto(
        String paginationMethod,
        String publishedAtStart,
        String publishedAtEnd,
        int page,
        int size
) {
}
