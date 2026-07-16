package com.example.Balenz.dailyfocus.service;

import com.example.Balenz.dailyfocus.dto.DailyFocusCacheDto;
import com.example.Balenz.dailyfocus.dto.DailyFocusKeywordDto;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.service.KeywordService;
import com.example.Balenz.scrap.service.KeywordScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyFocusService {

    private final KeywordService keywordService;
    private final KeywordScrapService keywordScrapService;
    private final DailyFocusCacheService dailyFocusCacheService;

    public List<DailyFocusKeywordDto> getDailyFocus(Long userId) {
        LocalDate serviceDate = keywordService.getCurrentServiceDate();

        List<DailyFocusKeywordDto> result = new ArrayList<>();

        for (Category category : Category.values()) {
            DailyFocusCacheDto dailyFocusCache = dailyFocusCacheService.getDailyFocusCache(category, serviceDate);
            if (dailyFocusCache == null) continue;

            // 스크랩 여부 확인
            boolean isScraped = keywordScrapService.isScraped(dailyFocusCache.id(), userId);

            result.add(toDailyFocusKeywordDto(dailyFocusCache, isScraped));
        }

        return result;
    }

    private DailyFocusKeywordDto toDailyFocusKeywordDto(DailyFocusCacheDto dailyFocusCacheDto, boolean isScraped) {
        return new DailyFocusKeywordDto(
                dailyFocusCacheDto.id(),
                dailyFocusCacheDto.name(),
                dailyFocusCacheDto.imageUrl(),
                dailyFocusCacheDto.articleCount(),
                dailyFocusCacheDto.articles(),
                isScraped
        );
    }

}
