package com.example.Balenz.report.service;

import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.global.email.EmailService;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.report.dto.ReportSubmitRequestDto;
import com.example.Balenz.report.entity.ProblemType;
import com.example.Balenz.report.entity.Report;
import com.example.Balenz.report.entity.ReportTargetType;
import com.example.Balenz.report.repository.ReportRepository;
import com.example.Balenz.user.entity.User;
import com.example.Balenz.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ArticleRepository articleRepository;
    private final KeywordRepository keywordRepository;
    private final ReportRepository reportRepository;
    private final AuthService authService;
    private final EmailService emailService;

    @Transactional
    public void submitReport(ReportSubmitRequestDto requestDto, Long userId) {
        // 해당 targetId의 Article이나 Keyword가 존재하는지 확인
        ReportTargetType targetType = requestDto.targetType();
        Long targetId = requestDto.targetId();
        switch (targetType) {
            case ARTICLE -> articleRepository.findById(targetId).orElseThrow(
                    () -> new BaseException(ErrorCode.ARTICLE_NOT_FOUND, "해당 id의 기사가 존재하지 않습니다."));
            case KEYWORD -> keywordRepository.findById(targetId).orElseThrow(
                    () -> new BaseException(ErrorCode.KEYWORD_NOT_FOUND, "해당 id의 키워드가 존재하지 않습니다."));
        }

        ProblemType problemType = requestDto.problemType();
        String content = requestDto.content();
        if (problemType == ProblemType.ETC && (content == null || content.isBlank())) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE, "문제에서 '기타'를 선택한 경우 추가 설명이 필수입니다.");
        }

        User reporter;
        if (userId == null) {
            reporter = null;
        } else {
            reporter = authService.getCurrentUser(userId);
        }

        Report report = reportRepository.save(
                Report.builder()
                        .targetType(targetType)
                        .targetId(targetId)
                        .problemType(problemType)
                        .content(content)
                        .reporter(reporter).build()
        );

        // DB에 저장 후 이메일 전송
        try {
            emailService.sendEmail(
                    "[문제 제보] 문제 제보가 접수되었습니다. #" + report.getId(),
                    """
                    - 제보 ID: %d
                    
                    - 대상 타입 (Article / Keyword): %s
                    - 대상 ID: %d
                    - 문제 유형: %s
                    - 추가 설명: %s
                    
                    - 제보자: %s
                    """.formatted(
                            report.getId(),
                            report.getTargetType(),
                            report.getTargetId(),
                            report.getProblemType(),
                            report.getContent() != null ? report.getContent() : "내용 없음",
                            report.getReporter() != null ? report.getReporter().getEmail() : "비회원"
                    )
            );
        } catch (Exception e) {
            log.error("문제 제보 이메일 전송 실패 - reportId={}", report.getId(), e);
        }
    }

}
