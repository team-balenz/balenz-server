package com.example.Balenz.article.entity;

import com.example.Balenz.global.entity.BaseTimeEntity;
import com.example.Balenz.scope.entity.Scope;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String articleUrl;

    private String summary;

    // 가치 프레임 점수
    @Column(nullable = false)
    private Double valueScore;

    // 규범 프레임 점수
    @Column(nullable = false)
    private Double normScore;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FrameType frameType;

    // VALUE 독자 조회수
    @Column(nullable = false)
    private Long valueUserViewCount = 0L;

    // NEUTRAL 독자 조회수
    @Column(nullable = false)
    private Long neutralUserViewCount = 0L;

    // NORM 독자 조회수
    @Column(nullable = false)
    private Long normUserViewCount = 0L;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_agency_id", nullable = false)
    private NewsAgency newsAgency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    public void setScope(Scope scope) {
        if (this.scope != null) {
            this.scope.getArticles().remove(this);
        }

        this.scope = scope;

        if (scope != null && !scope.getArticles().contains(this)) {
            scope.getArticles().add(this);
        }
    }

}
