package com.example.Balenz.keyword.service;

import com.example.Balenz.keyword.dto.KeywordSaveRequestDto;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminKeywordService {

    private final KeywordRepository keywordRepository;
    private final KeywordService keywordService;

    @Transactional
    public void saveKeyword(KeywordSaveRequestDto keywordSaveRequestDto) {
        keywordRepository.save(
                Keyword.builder()
                        .name(keywordSaveRequestDto.getName())
                        .category(keywordSaveRequestDto.getCategory())
                        .thumbnailUrl(keywordSaveRequestDto.getThumbnailUrl())
                        .serviceDate(keywordService.getPreparedServiceDate()).build()
        );
    }

}
