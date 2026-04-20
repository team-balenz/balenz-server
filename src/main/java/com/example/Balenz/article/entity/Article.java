package com.example.Balenz.article.entity;

import com.example.Balenz.global.entity.BaseTimeEntity;
import com.example.Balenz.scope.entity.Keyword;
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

    @Column(nullable = false, unique = true, length = 1000)
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
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    public void setKeyword(Keyword keyword) {
        if (this.keyword != null) {
            this.keyword.getArticles().remove(this);
        }

        this.keyword = keyword;

        if (keyword != null && !keyword.getArticles().contains(this)) {
            keyword.getArticles().add(this);
        }
    }

}
