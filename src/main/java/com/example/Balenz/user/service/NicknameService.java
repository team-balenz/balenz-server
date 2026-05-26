package com.example.Balenz.user.service;

import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private static final List<String> ADJECTIVES = List.of(
            "나른한", "행복한", "설레는", "졸린", "신난", "평온한", "수줍은", "느긋한", "몽환적인", "엉뚱한",
            "차분한", "들뜬", "용감한", "따뜻한", "활발한", "기분좋은", "자유로운", "반짝이는", "낙천적인", "감성적인",
            "장난스러운", "귀여운", "사랑스러운", "재빠른", "부드러운", "상냥한", "유쾌한", "산뜻한", "포근한", "싱그러운",
            "밝은", "화사한", "조용한", "씩씩한", "청량한", "다정한", "영리한", "순수한", "친근한", "개구쟁이같은",
            "활기찬", "여유로운", "우아한", "소중한", "명랑한", "재치있는", "든든한", "깔끔한", "고요한", "매력적인",
            "생기있는", "희망찬", "선명한", "특별한", "멋진", "세련된", "청순한", "발랄한", "빛나는", "편안한",
            "향기로운", "순한", "온화한", "감각적인", "호기심많은", "친절한", "똑똑한", "용맹한", "로맨틱한", "사려깊은",
            "해맑은", "미소짓는", "여린", "깜찍한", "신비로운", "순진한", "기특한", "부지런한", "반듯한", "듬직한",
            "재능있는", "영롱한", "유연한", "담백한", "감미로운", "산들산들한", "맑은", "통통튀는", "활짝웃는", "눈부신",
            "청명한", "말랑말랑한", "폭신한", "사랑가득한", "쾌활한", "센스있는", "기운찬", "두근두근한", "반가운", "행복가득한"
    );

    private static final List<String> ANIMALS = List.of(
            "원숭이", "고양이", "강아지", "햄스터", "토끼", "여우", "늑대", "펭귄", "수달", "호랑이",
            "사자", "알파카", "판다", "고슴도치", "다람쥐", "오리", "병아리", "독수리", "치타", "북극곰",
            "코알라", "카피바라", "너구리", "미어캣", "물개", "참새", "부엉이", "라쿤", "고래", "해달",
            "돌고래", "사슴", "말", "양", "염소", "소", "돼지", "닭", "공작", "앵무새",
            "까마귀", "백조", "매", "해마", "두루미", "비둘기", "참치", "상어", "문어", "오징어",
            "고등어", "연어", "가오리", "불가사리", "해파리", "바다사자", "바다표범", "코끼리", "기린", "하마",
            "캥거루", "치와와", "시바견", "리트리버", "퓨마", "재규어", "표범", "타조", "펠리컨", "플라밍고",
            "청설모", "두더지", "족제비", "수리부엉이", "삵", "스컹크", "비버", "친칠라", "라마", "나무늘보",
            "도마뱀", "카멜레온", "악어", "거북이", "이구아나", "날다람쥐", "고니", "꿀벌", "나비", "무당벌레",
            "사마귀", "잠자리", "개미", "반딧불이", "메추리", "코뿔소", "들소", "산양", "바다거북", "청개구리"
    );
    private final UserRepository userRepository;

    /** 임시 닉네임 생성 */
    public String generateRandomNickname() {
        for (int i = 0; i < 10; i++) {
            String adjective = ADJECTIVES.get(ThreadLocalRandom.current().nextInt(ADJECTIVES.size()));
            String animal = ANIMALS.get(ThreadLocalRandom.current().nextInt(ANIMALS.size()));
            int number = ThreadLocalRandom.current().nextInt(1000, 10000);
            String nickname = adjective + animal + number;
            if (!userRepository.existsByNickname(nickname)) {
                return nickname;
            }
        }

        throw new BaseException(ErrorCode.NICKNAME_GENERATION_FAILED);
    }

}
