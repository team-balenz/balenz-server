package com.example.Balenz.keyword.repository;

import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findTop7ByCategoryAndServiceDateOrderByViewCountDescIdDesc(Category category, LocalDate serviceDate);
    List<Keyword> findTop7ByServiceDateOrderByViewCountDescIdDesc(LocalDate serviceDate);
    Optional<Keyword> findByNameAndCategoryAndServiceDate(String name, Category category, LocalDate serviceDate);
    List<Keyword> findTop6ByServiceDateOrderByViewCountDescIdDesc(LocalDate serviceDate);
    List<Keyword> findByNameContaining(String query);
}
