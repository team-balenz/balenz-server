package com.example.Balenz.internal.service;

import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.internal.dto.ExternalArticleRequestDto;
import com.example.Balenz.internal.dto.ExternalArticleResponseDto;
import com.example.Balenz.internal.dto.TestArticleResponseDto;
import com.example.Balenz.internal.entity.TestArticle;
import com.example.Balenz.internal.repository.TestArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestArticleService {

    private final TestArticleRepository testArticleRepository;
    @Value("${NEWS_API_URL}")
    private String NEWS_API_URL;

    @Value("${NEWS_API_KEY}")
    private String NEWS_API_KEY;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public int saveTestArticleData() {
        int page = 1;
        int savedArticleCount = 0;

        while (true) {
            ExternalArticleResponseDto response;

            try {
                response = requestArticles(page);
            } catch (BaseException e) {
                // 실패한 페이지부터 중단하고 지금까지 저장한 건수를 반환한다.
                log.warn(
                        "외부 API {}페이지 조회 실패. 지금까지 저장한 {}건만 반영합니다. 원인: {}",
                        page,
                        savedArticleCount,
                        e.getMessage()
                );
                break;
            }

            if (response.getItems().isEmpty()) {
                break;
            }

            savedArticleCount += saveTestArticles(response.getItems());
            page++;
        }

        return savedArticleCount;
    }

    /** TestArticle 저장 */
    private int saveTestArticles(List<ExternalArticleResponseDto.ExternalArticleDto> articles) {
        List<String> forbiddenCategories = List.of("인물", "종합", "기타", "오피니언");
        int count = 0;

        for (ExternalArticleResponseDto.ExternalArticleDto article : articles) {
            List<String> categories = article.getCategories();

            if (categories != null
                    && forbiddenCategories.stream().anyMatch(categories::contains)) { // forbidden에 속하는 카테고리를 포함하는 경우 저장 X
                continue;
            }

            testArticleRepository.save(
                    new TestArticle(
                            article.getTitle(),
                            article.getBody(),
                            categories
                    ));

            count++;
        }

        return count;
    }

    /** 외부 API로부터 기사 조회 */
    private ExternalArticleResponseDto requestArticles(int page) {
        try {
            ExternalArticleRequestDto request = new ExternalArticleRequestDto(
                    "PAGE",
                    "2026-07-01",
                    "2026-07-15",
                    page,
                    100
            );

            byte[] requestBody = objectMapper
                    .writeValueAsString(request)
                    .getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", NEWS_API_KEY);
            headers.setContentLength(requestBody.length);
            headers.set(HttpHeaders.CONNECTION, "close");

            // 이 외부 API는 JSON 본문 전송 후 연결을 비정상 종료함
            // -> execute()로 응답 스트림을 직접 읽어 연결 종료 전까지 받은 본문 보존
            byte[] responseBody = restTemplate.execute(
                    NEWS_API_URL,
                    HttpMethod.POST,
                    clientRequest -> {
                        clientRequest.getHeaders().putAll(headers);
                        clientRequest.getBody().write(requestBody);
                    },
                    response -> {
                        // 수신한 응답 누적
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                        try {
                            response.getBody().transferTo(buffer);
                        } catch (IOException readException) {
                            byte[] partialBody = buffer.toByteArray();

                            // 응답 자체가 비어있는 경우 복구 X
                            if (partialBody.length == 0) {
                                throw readException;
                            }

                            try {
                                // 완전한 JSON일 때만 부분 응답 사용
                                objectMapper.readTree(partialBody);
                                log.warn(
                                        "외부 API 연결이 비정상 종료되었으나 수신된 응답은 완전한 JSON으로 확인되어 정상 처리했습니다. status: {}, contentLength: {}, 원인: {}",
                                        response.getStatusCode(),
                                        partialBody.length,
                                        readException.getMessage()
                                );
                                return partialBody;
                            } catch (Exception jsonException) {
                                readException.addSuppressed(jsonException);
                                throw readException;
                            }
                        }

                        return buffer.toByteArray();
                    }
            );

            if (responseBody == null || responseBody.length == 0) {
                throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "외부 API로부터 응답을 받아오지 못했습니다.");
            }

            return objectMapper.readValue(
                    responseBody,
                    ExternalArticleResponseDto.class
            );
        } catch (ResourceAccessException e) {
            log.error("외부 API 연결 에러 - " + e.getMessage());
            throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "외부 API 연결에 실패했습니다.");
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("외부 API 에러 - " + e.getMessage());
            throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "외부 API에서 알 수 없는 오류가 발생했습니다.");
        }
    }

    public List<TestArticleResponseDto> getTestArticleData(int page, int size) {
        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        return testArticleRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(testArticle -> new TestArticleResponseDto(
                        testArticle.getId(),
                        testArticle.getTitle(),
                        testArticle.getContent()
                )).toList();
    }

}
