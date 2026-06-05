package com.example.Balenz.report.entity;

import com.example.Balenz.global.entity.BaseTimeEntity;
import com.example.Balenz.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType;

    // 기사 / 키워드 id
    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemType problemType;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @Builder
    public Report(ReportTargetType targetType, Long targetId, ProblemType problemType, String content, User reporter) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.problemType = problemType;
        this.content = content;
        this.reporter = reporter;
    }

}
