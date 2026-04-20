package com.example.Balenz.keyword.dto;

import com.example.Balenz.keyword.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeywordSaveRequestDto {

    @NotBlank
    private String name;

    private String thumbnailUrl;

    @NotNull
    private Category category;

}
