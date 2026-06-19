package com.example.Balenz.article.external.prompt;

public class SummaryPrompt {

    public static String create(String title, String content) {
        return """
                당신은 뉴스 기사 요약 전문가입니다.
                아래 기사를 요약하세요.
                
                [요약 규칙]
                1. 기사의 핵심 사실만 포함하세요.
                2. 기사에 등장하는 주요 인물, 기관, 사건을 포함하세요.
                3. 3~4문장 이내로 작성하세요.
                4. 기사에 없는 내용은 절대 추가하지 마세요.
                5. 모든 문장은 '~했습니다', '~라고 밝혔습니다'와 같은 존댓말 문체로 작성하세요.
                
                [기사 제목]
                %s
                
                [기사 본문]
                %s
                
                [출력 형식]
                - 반드시 순수 JSON만 반환하세요.
                - 응답은 반드시 여는 중괄호로 시작하고 닫는 중괄호로 끝나야 합니다.
                {
                    "summary" : "요약"
                }
                """.formatted(title, content);
    }

}
