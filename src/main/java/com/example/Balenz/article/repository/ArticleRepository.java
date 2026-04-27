package com.example.Balenz.article.repository;

import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    long countByKeywordIdAndFrameType(Long keywordId, FrameType frameType);
    // 키워드 내 각 프레임타입별로 각각 가장 조회수가 높은 기사 조회
    @Query(value = """
        SELECT *
        FROM Article 
        WHERE keyword_id = :keywordId
          AND frame_type = :frameType
        ORDER BY (value_user_view_count + neutral_user_view_count + norm_user_view_count) DESC, id ASC
        LIMIT 1
    """, nativeQuery = true)
    Optional<Article> findTopByKeyword_IdAndFrameTypeOrderByViewCountDesc(@Param("keywordId") Long keywordId,
                                                     @Param("frameType") String frameType);

    List<Article> findTop4ByKeyword_IdAndFrameTypeOrderByPublishedAtDesc(Long keywordId, FrameType frameType);
}
