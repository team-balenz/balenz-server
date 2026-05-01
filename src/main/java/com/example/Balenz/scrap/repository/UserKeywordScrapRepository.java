package com.example.Balenz.scrap.repository;

import com.example.Balenz.scrap.entity.UserKeywordScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKeywordScrapRepository extends JpaRepository<UserKeywordScrap, Long> {
    boolean existsByUser_IdAndKeyword_Id(Long userId, Long keywordId);
}
