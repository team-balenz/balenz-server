package com.example.Balenz.dailyfocus.service;

import com.example.Balenz.dailyfocus.dto.DailyFocusKeywordDto;
import com.example.Balenz.dailyfocus.dto.DailyFocusUpdateRequestDto;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDailyFocusService {

    private final DailyFocusService dailyFocusService;
    private final KeywordRepository keywordRepository;
    private final KeywordService keywordService;
    private final DailyFocusCacheService dailyFocusCacheService;

    public List<DailyFocusKeywordDto> updateDailyFocusKeyword(DailyFocusUpdateRequestDto updateRequestDto,
                                                              Long userId) {
        String name = updateRequestDto.getName();
        Category category = updateRequestDto.getCategory();
        LocalDate serviceDate = keywordService.getCurrentServiceDate();
        Keyword keyword = keywordRepository.findByNameAndCategoryAndServiceDate(name, category, serviceDate)
                .orElseThrow(() -> new BaseException(ErrorCode.KEYWORD_NOT_FOUND, "해당 카테고리에 해당 이름의 키워드가 존재하지 않습니다."));

        dailyFocusCacheService.refreshDailyFocusCache(keyword, category, serviceDate);

        // 전체 데일리 포커스 키워드 반환
        return dailyFocusService.getDailyFocus(userId);
    }

}
