package com.example.Balenz.article.repository;

import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    long countByKeywordIdAndFrameTypeIn(Long keywordId, List<FrameType> frameTypes);

    // STRONG + 일반 이념 프레임타입 합쳐서 각각 가장 조회수가 높은 기사 조회
    @Query("""
    SELECT a
    FROM Article a
    WHERE a.keyword.id = :keywordId
      AND a.frameType IN :frameTypes
    ORDER BY ( a.valueUserViewCount + a.neutralUserViewCount + a.normUserViewCount ) DESC,
    a.id ASC
""")
    List<Article> findTopByKeywordIdAndFrameTypeInOrderByViewCountDesc(
            @Param("keywordId") Long keywordId,
            @Param("frameTypes") List<FrameType> frameTypes,
            Pageable pageable
    );

    List<Article> findTop4ByKeyword_IdAndFrameTypeOrderByPublishedAtDesc(Long keywordId, FrameType frameType);

    // 키워드 내 각 프레임타입별로 각각 조회수가 높은 기사 조회
    @Query("""
    SELECT a
    FROM Article a
    WHERE a.keyword.id = :keywordId
      AND a.frameType = :frameType
    ORDER BY ( a.valueUserViewCount + a.neutralUserViewCount + a.normUserViewCount ) DESC,
    a.id ASC
""")
    List<Article> findByKeyword_IdAndFrameTypeOrderByViewCountDesc(
            @Param("keywordId") Long keywordId,
            @Param("frameType") FrameType frameType,
            Pageable pageable
    );

    List<Article> findTop8ByKeyword_ServiceDateOrderByValueUserViewCountDesc(LocalDate serviceDate);
    List<Article> findTop8ByKeyword_ServiceDateOrderByNormUserViewCountDesc(LocalDate serviceDate);
    List<Article> findByKeyword_ServiceDateAndFrameTypeIn(LocalDate serviceDate, List<FrameType> frameTypes, Pageable pageable);
    @Query("""
    SELECT a
    FROM Article a
    WHERE REPLACE(a.title, ' ', '') LIKE CONCAT('%', REPLACE(:query, ' ', ''), '%')
""")
    List<Article> searchByTitle(@Param("query") String query);

    // 키워드 내 기사 조회수 순 전체 조회
    @Query("""
    SELECT a
    FROM Article a
    WHERE a.keyword.id = :keywordId
    ORDER BY ( a.valueUserViewCount + a.neutralUserViewCount + a.normUserViewCount ) DESC,
    a.id ASC
""")
    List<Article> findByKeywordIdOrderByTotalViewCountDesc(@Param("keywordId") Long keywordId);
}
