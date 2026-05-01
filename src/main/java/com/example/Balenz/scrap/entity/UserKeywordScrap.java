package com.example.Balenz.scrap.entity;

import com.example.Balenz.global.entity.BaseTimeEntity;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "keyword_id"}
        )
)
public class UserKeywordScrap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Builder
    public UserKeywordScrap(User user, Keyword keyword) {
        this.user = user;
        this.keyword = keyword;
    }

}
