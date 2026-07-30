package com.example.Balenz.internal.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExternalArticleResponseDto {

    private List<ExternalArticleDto> items;

    @Data
    public static class ExternalArticleDto {

        private String title;

        private String body;

        private List<String> categories;

    }

}
