package com.example.Balenz.user.entity;

import com.example.Balenz.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    // 소셜 로그인일 경우 null
    private String password;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Ideology ideology;

    private Integer yearOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @Builder
    public User(String nickname, String email, String password, Role role, String imageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = (role == null) ? Role.ROLE_USER : role;
        this.imageUrl = imageUrl;
    }

    public void addSocialAccount(SocialAccount socialAccount) {
        this.socialAccounts.add(socialAccount);
        socialAccount.setUser(this);
    }

}
