package com.example.Balenz.article.repository;

import com.example.Balenz.article.entity.NewsAgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsAgencyRepository extends JpaRepository<NewsAgency, Long> {
    Optional<NewsAgency> findByName(String name);
}
