package com.example.Balenz.scrap.repository;

import com.example.Balenz.scrap.entity.UserArticleScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserArticleScrapRepository extends JpaRepository<UserArticleScrap, Long> {
    boolean existsByUser_IdAndArticle_Id(Long userId, Long articleId);
}
