package com.example.Balenz.article.external.prompt;

public class IdeologyClassifiablePrompt {

    public static String create(String articleContent) {
        return """
                당신은 한국 언론 기사 분류 전문가입니다.
                아래 기사가 한국의 진보·보수 이념 스펙트럼으로 분류 가능한 기사인지 판단하세요.
                
                [분류 불가 (false) 조건 - 하나라도 해당하면 false]
                - 한국 이념 프레임과 무관한 순수 해외 이슈
                - 화재, 교통사고, 자연재해 등 단순 사건사고
                - 이념과 무관한 문화·예술 기사
                
                [주의]
                - 경제·사회·외교 기사라도 한국 이념 프레임이 적용 가능하면 true로 판단하세요.
                - 판단이 애매한 경우 true로 처리하세요.
                
                [출력 형식]
                - 반드시 순수 JSON만 반환하세요.
                - isClassifiable 값은 true 또는 false 중 하나여야 합니다.
                - 응답은 반드시 여는 중괄호로 시작하고 닫는 중괄호로 끝나야 합니다.
                { "isClassifiable" : true } 또는 { "isClassifiable" : false }
                
                [기사 본문]
                %s
                """.formatted(articleContent);
    }

}
