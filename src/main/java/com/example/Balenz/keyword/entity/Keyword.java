package com.example.Balenz.keyword.entity;

import com.example.Balenz.article.entity.Article;
import com.example.Balenz.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"name", "category", "service_date"}
        )
)
public class Keyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String thumbnailUrl;

    private String summary;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false, name="service_date")
    private LocalDate serviceDate;

    @OneToMany(mappedBy = "keyword")
    private List<Article> articles = new ArrayList<>();

    @Builder
    public Keyword(String name, String thumbnailUrl, String summary, Category category, LocalDate serviceDate) {
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.summary = summary;
        this.category = category;
        this.serviceDate = serviceDate;
    }

}
