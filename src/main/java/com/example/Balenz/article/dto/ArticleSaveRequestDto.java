package com.example.Balenz.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ArticleSaveRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String articleUrl;

    private String imageUrl;

    @NotBlank
    private String newsAgencyName;

    @NotNull
    private LocalDate publishedAt;

}
