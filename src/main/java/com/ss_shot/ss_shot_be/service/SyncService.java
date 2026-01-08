package com.ss_shot.ss_shot_be.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ss_shot.ss_shot_be.dto.request.ScreenshotSyncItem;
import com.ss_shot.ss_shot_be.dto.request.SyncRequest;
import com.ss_shot.ss_shot_be.dto.response.SyncResponse;
import com.ss_shot.ss_shot_be.entity.Category;
import com.ss_shot.ss_shot_be.entity.ScreenshotMetadata;
import com.ss_shot.ss_shot_be.entity.User;
import com.ss_shot.ss_shot_be.repository.ScreenshotMetadataRepository;
import com.ss_shot.ss_shot_be.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SyncService {

    private final ScreenshotMetadataRepository metadataRepository;
    private final UserRepository userRepository;
    private final CategoryClassifier categoryClassifier;
    private final ObjectMapper objectMapper;

    public SyncService(ScreenshotMetadataRepository metadataRepository,
                       UserRepository userRepository,
                       CategoryClassifier categoryClassifier) {
        this.metadataRepository = metadataRepository;
        this.userRepository = userRepository;
        this.categoryClassifier = categoryClassifier;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Transactional
    public SyncResponse sync(Long userId, SyncRequest request) {
        User user = userRepository.getReferenceById(userId);

        List<SyncResponse.SyncResultItem> results = new ArrayList<>();
        int syncedCount = 0;
        int failedCount = 0;

        if (request.getScreenshots() == null || request.getScreenshots().isEmpty()) {
            return SyncResponse.builder()
                    .synced(0)
                    .failed(0)
                    .results(results)
                    .build();
        }

        // 기존 데이터 조회 (bulk)
        List<String> localIds = request.getScreenshots().stream()
                .map(ScreenshotSyncItem::getLocalId)
                .toList();

        Map<String, ScreenshotMetadata> existingMap = metadataRepository
                .findByUserIdAndLocalIdIn(userId, localIds)
                .stream()
                .collect(Collectors.toMap(ScreenshotMetadata::getLocalId, m -> m));

        for (ScreenshotSyncItem item : request.getScreenshots()) {
            try {
                ScreenshotMetadata metadata = existingMap.get(item.getLocalId());
                Category category = categoryClassifier.classify(item.getFullText());
                String blocksJson = convertBlocksToJson(item.getBlocks());

                if (metadata == null) {
                    // 새로운 데이터 생성
                    metadata = ScreenshotMetadata.builder()
                            .user(user)
                            .localId(item.getLocalId())
                            .fullText(item.getFullText())
                            .ocrJson(blocksJson)
                            .category(category)
                            .capturedAt(item.getCapturedAt())
                            .isDeleted(false)
                            .isFavorite(false)
                            .build();
                } else {
                    // 기존 데이터 업데이트
                    metadata.setFullText(item.getFullText());
                    metadata.setOcrJson(blocksJson);
                    metadata.setCategory(category);
                    metadata.setCapturedAt(item.getCapturedAt());
                    metadata.setIsDeleted(false);  // 다시 동기화되면 삭제 상태 해제
                }

                metadata = metadataRepository.save(metadata);

                results.add(SyncResponse.SyncResultItem.builder()
                        .localId(item.getLocalId())
                        .serverId(metadata.getId())
                        .category(category.name())
                        .status("SUCCESS")
                        .build());

                syncedCount++;

            } catch (Exception e) {
                log.error("Failed to sync item: {}", item.getLocalId(), e);

                results.add(SyncResponse.SyncResultItem.builder()
                        .localId(item.getLocalId())
                        .serverId(null)
                        .category(null)
                        .status("FAILED")
                        .build());

                failedCount++;
            }
        }

        return SyncResponse.builder()
                .synced(syncedCount)
                .failed(failedCount)
                .results(results)
                .build();
    }

    private String convertBlocksToJson(List<ScreenshotSyncItem.OcrBlock> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(blocks);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert blocks to JSON", e);
            return null;
        }
    }
}
