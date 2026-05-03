package com.example.Balenz.keyword.dto;

import com.example.Balenz.article.dto.SimpleArticleDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HotIssueDataDto {

    List<KeywordAndArticleDto> keywordAndArticles;

    @Data
    @Builder
    public static class KeywordAndArticleDto {

        Long id;

        String name;

        List<SimpleArticleDto> articles;

    }

}
