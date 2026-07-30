package com.example.Balenz.internal.repository;

import com.example.Balenz.internal.entity.TestArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestArticleRepository extends JpaRepository<TestArticle, Long> {
}
