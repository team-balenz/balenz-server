package com.example.Balenz.keyword.service;

import com.example.Balenz.article.dto.SimpleArticleDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.keyword.dto.HotIssueDataDto;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HotKeywordService {

    private final KeywordRepository keywordRepository;
    private final KeywordService keywordService;
    private final ArticleRepository articleRepository;

    /** 이념 관점 페이지에서 hot-issue 섹션 데이터 조회 */
    public HotIssueDataDto getHotIssueData() {
        // 1. 오늘 조회수가 가장 높은 키워드 조회
        LocalDate serviceDate = keywordService.getCurrentServiceDate();
        List<Keyword> keywords = keywordRepository.findTop6ByServiceDateOrderByViewCountDescIdDesc(serviceDate);

        if (keywords.isEmpty()) {
            return HotIssueDataDto.builder()
                    .keywordAndArticles(List.of()).build();
        }

        List<HotIssueDataDto.KeywordAndArticleDto> keywordAndArticles = new ArrayList<>();

        // 2. 각 키워드에서 인기있는 기사 2개 조회
        for (Keyword keyword : keywords) {
            List<Article> strongValue = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.STRONG_VALUE.name());
            List<Article> value = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.VALUE.name());
            List<Article> neutral = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.NEUTRAL.name());
            List<Article> norm = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.NORM.name());
            List<Article> strongNorm = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.STRONG_NORM.name());

            // 각 프레임타입별로 기사 2개씩 조회 후 그 중 랜덤으로 2개 반환
            List<Article> candidates
                    = Stream.of(strongValue.stream(), value.stream(), neutral.stream(), norm.stream(), strongNorm.stream())
                    .flatMap(s -> s).toList();

            List<SimpleArticleDto> articles = pick(candidates, 2).stream()
                    .map(this::toSimpleArticleDto).toList();

            HotIssueDataDto.KeywordAndArticleDto keywordAndArticle = HotIssueDataDto.KeywordAndArticleDto.builder()
                    .id(keyword.getId())
                    .name(keyword.getName())
                    .articles(articles).build();

            keywordAndArticles.add(keywordAndArticle);
        }

        return HotIssueDataDto.builder()
                .keywordAndArticles(keywordAndArticles).build();
    }

    private SimpleArticleDto toSimpleArticleDto(Article article) {
        return SimpleArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .newsAgencyName(article.getNewsAgency().getName())
                .frameType(article.getFrameType()).build();
    }

    private List<Article> pick(List<Article> list, int count) {
        ArrayList<Article> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

}
