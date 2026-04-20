package com.example.Balenz.scope.entity;

import com.example.Balenz.article.entity.Article;
import com.example.Balenz.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @OneToMany(mappedBy = "keyword")
    private List<Article> articles = new ArrayList<>();

}
