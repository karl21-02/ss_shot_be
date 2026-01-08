package com.ss_shot.ss_shot_be.dto.response;

import com.ss_shot.ss_shot_be.entity.Category;
import com.ss_shot.ss_shot_be.entity.ScreenshotMetadata;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenshotResponse {

    private Long id;

    private String localId;

    private String fullText;

    private String category;

    private LocalDateTime capturedAt;

    private Boolean isFavorite;

    public static ScreenshotResponse from(ScreenshotMetadata metadata) {
        return ScreenshotResponse.builder()
                .id(metadata.getId())
                .localId(metadata.getLocalId())
                .fullText(metadata.getFullText())
                .category(metadata.getCategory().name())
                .capturedAt(metadata.getCapturedAt())
                .isFavorite(metadata.getIsFavorite())
                .build();
    }
}
