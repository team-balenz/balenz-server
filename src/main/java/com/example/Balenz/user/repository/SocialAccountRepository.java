package com.example.Balenz.user.repository;

import com.example.Balenz.user.entity.Provider;
import com.example.Balenz.user.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderUserId(Provider provider, String providerId);
}
