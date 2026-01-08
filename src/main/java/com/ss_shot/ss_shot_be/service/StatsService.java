package com.ss_shot.ss_shot_be.service;

import com.ss_shot.ss_shot_be.dto.response.StatsResponse;
import com.ss_shot.ss_shot_be.entity.Category;
import com.ss_shot.ss_shot_be.repository.ScreenshotMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final ScreenshotMetadataRepository metadataRepository;

    @Transactional(readOnly = true)
    public StatsResponse getStats(Long userId) {
        // 전체 개수
        long total = metadataRepository.countByUserIdAndNotDeleted(userId);

        // 카테고리별 개수
        List<Object[]> categoryStats = metadataRepository.countByUserIdGroupByCategory(userId);
        Map<String, Long> categories = new HashMap<>();

        // 기본값 설정 (모든 카테고리 0으로 초기화)
        for (Category category : Category.values()) {
            categories.put(category.name(), 0L);
        }

        // 실제 값으로 업데이트
        for (Object[] row : categoryStats) {
            Category category = (Category) row[0];
            Long count = (Long) row[1];
            categories.put(category.name(), count);
        }

        // 이번 달 통계
        LocalDateTime startOfMonth = LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        long addedThisMonth = metadataRepository.countAddedThisMonth(userId, startOfMonth);
        long deletedThisMonth = metadataRepository.countDeletedThisMonth(userId, startOfMonth);

        return StatsResponse.builder()
                .total(total)
                .categories(categories)
                .thisMonth(StatsResponse.ThisMonth.builder()
                        .added(addedThisMonth)
                        .deleted(deletedThisMonth)
                        .build())
                .build();
    }
}
