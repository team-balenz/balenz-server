package com.example.Balenz.keyword.repository;

import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findTop6ByCategoryAndServiceDateOrderByViewCountDescIdDesc(Category category, LocalDate serviceDate);
    Optional<Keyword> findByNameAndCategoryAndServiceDate(String name, Category category, LocalDate serviceDate);
    List<Keyword> findTop6ByServiceDateOrderByViewCountDescIdDesc(LocalDate serviceDate);
    @Query("""
    SELECT k
    FROM Keyword k
    WHERE REPLACE(k.name, ' ', '') LIKE CONCAT('%', REPLACE(:query, ' ', ''), '%')
""")
    List<Keyword> searchByName(@Param("query") String query);
    Optional<Keyword> findTopByCategoryAndServiceDateOrderByViewCountDescIdDesc(Category category, LocalDate serviceDate);
}
