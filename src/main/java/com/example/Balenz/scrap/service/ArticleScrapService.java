package com.example.Balenz.scrap.service;

import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.scrap.entity.UserArticleScrap;
import com.example.Balenz.scrap.repository.UserArticleScrapRepository;
import com.example.Balenz.user.entity.User;
import com.example.Balenz.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleScrapService {

    private final ArticleRepository articleRepository;
    private final AuthService authService;
    private final UserArticleScrapRepository userArticleScrapRepository;

    @Transactional
    public void scrapArticle(Long articleId, Long userId) {
        User user = authService.getCurrentUser(userId);

        Article article = articleRepository.findById(articleId).orElseThrow(
                () -> new BaseException(ErrorCode.ARTICLE_NOT_FOUND, "해당 id의 기사를 찾을 수 없습니다."));

        if (userArticleScrapRepository.existsByUser_IdAndArticle_Id(userId, articleId)) {
            throw new BaseException(ErrorCode.ARTICLE_SCRAP_ALREADY_EXISTS, "이미 스크랩한 기사입니다.");
        }

        userArticleScrapRepository.save(
                UserArticleScrap.builder()
                        .user(user)
                        .article(article).build());
    }

}
