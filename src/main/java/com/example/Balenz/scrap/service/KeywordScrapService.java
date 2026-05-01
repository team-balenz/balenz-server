package com.example.Balenz.scrap.service;

import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.scrap.entity.UserKeywordScrap;
import com.example.Balenz.scrap.repository.UserKeywordScrapRepository;
import com.example.Balenz.user.entity.User;
import com.example.Balenz.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeywordScrapService {

    private final AuthService authService;
    private final KeywordRepository keywordRepository;
    private final UserKeywordScrapRepository userKeywordScrapRepository;

    @Transactional
    public void scrapKeyword(Long keywordId, Long userId) {
        User user = authService.getCurrentUser(userId);

        Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(
                () -> new BaseException(ErrorCode.KEYWORD_NOT_FOUND, "해당 id의 키워드를 찾을 수 없습니다."));

        if (userKeywordScrapRepository.existsByUser_IdAndKeyword_Id(userId, keywordId)) {
            throw new BaseException(ErrorCode.KEYWORD_SCRAP_ALREADY_EXISTS, "이미 스크랩한 키워드입니다.");
        }

        userKeywordScrapRepository.save(
                UserKeywordScrap.builder()
                        .user(user)
                        .keyword(keyword).build());
    }

}
