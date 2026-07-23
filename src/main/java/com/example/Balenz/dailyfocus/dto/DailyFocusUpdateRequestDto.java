package com.example.Balenz.dailyfocus.dto;

import com.example.Balenz.keyword.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class DailyFocusUpdateRequestDto {

    @NotNull
    private Category category;

    @NotBlank
    private String name;

}
