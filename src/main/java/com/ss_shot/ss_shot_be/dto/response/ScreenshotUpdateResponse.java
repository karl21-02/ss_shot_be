package com.ss_shot.ss_shot_be.dto.response;

import com.ss_shot.ss_shot_be.entity.ScreenshotMetadata;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenshotUpdateResponse {

    private Long id;

    private String localId;

    private Boolean isDeleted;

    private Boolean isFavorite;

    private LocalDateTime updatedAt;

    public static ScreenshotUpdateResponse from(ScreenshotMetadata metadata) {
        return ScreenshotUpdateResponse.builder()
                .id(metadata.getId())
                .localId(metadata.getLocalId())
                .isDeleted(metadata.getIsDeleted())
                .isFavorite(metadata.getIsFavorite())
                .updatedAt(metadata.getUpdatedAt())
                .build();
    }
}
