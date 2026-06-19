package com.example.Balenz.article.external.prompt;

import java.util.List;

public class KeywordPrompt {

    public static String create(String title, String content, List<String> keywords) {
        String keywordList = keywords.isEmpty()
                ? "(없음)"
                : "- " + String.join("\n- ", keywords);
        return """
                당신은 뉴스 기사 분석 전문가입니다.
                기사에서 핵심 키워드 1개를 추출하세요.
                단, 아래 기존 키워드 목록 중 같은 주제를 의미하는 키워드가 있다면 새 키워드를 만들지 말고 기존 키워드를 선택하세요. 
                
                [기사 제목]
                %s
                
                [기사 본문]
                %s
                
                [기존 키워드]
                %s
                
                [출력 형식]
                - 반드시 순수 JSON만 반환하세요.
                - 응답은 반드시 여는 중괄호로 시작하고 닫는 중괄호로 끝나야 합니다.
                {
                    "keyword" : "키워드"
                }
                """.formatted(title, content, keywordList);
    }

}
