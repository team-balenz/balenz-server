package com.example.Balenz.article.external;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaudeApiClient {

    private final ObjectMapper objectMapper;

    @Value("${CLAUDE_API_KEY}")
    private String CLAUDE_API_KEY;

    public <T> T getResponse(String prompt, Long maxTokens, Class<T> responseType) {
        try {
            AnthropicClient client = AnthropicOkHttpClient.builder()
                    .apiKey(CLAUDE_API_KEY).build();

            MessageCreateParams params = MessageCreateParams.builder()
                    .model(Model.CLAUDE_HAIKU_4_5)
                    .maxTokens(maxTokens)
                    .addUserMessage(prompt).build();

            Message response = client.messages().create(params);

            String responseText = response.content().get(0).text().orElseThrow(
                    () -> new BaseException(ErrorCode.EXTERNAL_API_ERROR, "Claude API 응답에 텍스트가 없습니다.")
            ).text();

            if (responseText.isBlank())
                throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "Claude API 응답이 비어있습니다.");

            String json = cleanJson(responseText);
            return objectMapper.readValue(json, responseType);
        } catch (ResourceAccessException e) {
            log.error("Claude API 연결 에러 - " + e.getMessage());
            throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "Claude API 연결에 실패했습니다.");
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Claude API 에러 - " + e.getMessage());
            throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "Claude API에서 알 수 없는 오류가 발생했습니다.");
        }
    }

    private String cleanJson(String response) {
        String cleaned = response.trim();

        if (cleaned.startsWith("```")) {
            cleaned = cleaned
                    .replaceFirst("^```[a-zA-Z]*\\s*", "")
                    .replaceFirst("\\s*```$", "")
                    .trim();
        }

        return cleaned;
    }

}
