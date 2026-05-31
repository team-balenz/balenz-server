package com.example.Balenz.keyword.dto;

import com.example.Balenz.keyword.entity.DominantFrameType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeywordDto {

    private Long id;

    private String name;

    private String imageUrl;

    private ScopeSectionResponseDto.ArticleCountDto articleCount;

    // 어떤 관점의 기사가 많은지
    private DominantFrameType dominantFrameType;

}
