package com.ss_shot.ss_shot_be.service;

import com.ss_shot.ss_shot_be.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CategoryClassifier {

    private static final Map<Category, List<String>> KEYWORDS = Map.of(
            Category.FINANCE, List.of(
                    "입금", "출금", "잔액", "이체", "계좌", "결제", "카드", "은행",
                    "원", "금액", "대출", "금리", "통장", "ATM", "송금"
            ),
            Category.SHOPPING, List.of(
                    "배송", "장바구니", "주문", "결제완료", "쿠팡", "배민", "배달",
                    "택배", "구매", "상품", "쇼핑", "할인", "쿠폰", "적립"
            ),
            Category.SCHEDULE, List.of(
                    "초대", "약속", "예약", "월", "일", "시", "분",
                    "회의", "미팅", "알림", "일정", "캘린더", "오전", "오후", "PM", "AM"
            ),
            Category.HUMOR, List.of(
                    "ㅋㅋ", "ㅎㅎ", "ㅠㅠ", "ㅜㅜ", "짤", "밈", "웃긴", "ㅋㅋㅋ", "ㅎㅎㅎ"
            )
    );

    public Category classify(String text) {
        if (text == null || text.isBlank()) {
            return Category.OTHER;
        }

        String lowerText = text.toLowerCase();

        // 각 카테고리별 매칭 점수 계산
        int maxScore = 0;
        Category bestCategory = Category.OTHER;

        for (Map.Entry<Category, List<String>> entry : KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (lowerText.contains(keyword.toLowerCase())) {
                    score++;
                }
            }
            if (score > maxScore) {
                maxScore = score;
                bestCategory = entry.getKey();
            }
        }

        return bestCategory;
    }
}
