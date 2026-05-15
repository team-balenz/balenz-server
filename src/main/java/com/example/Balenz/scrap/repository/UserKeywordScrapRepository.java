package com.example.Balenz.scrap.repository;

import com.example.Balenz.scrap.entity.UserKeywordScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserKeywordScrapRepository extends JpaRepository<UserKeywordScrap, Long> {
    Optional<UserKeywordScrap> findByUser_IdAndKeyword_Id(Long userId, Long keywordId);
    boolean existsByUser_IdAndKeyword_Id(Long userId, Long keywordId);
}
