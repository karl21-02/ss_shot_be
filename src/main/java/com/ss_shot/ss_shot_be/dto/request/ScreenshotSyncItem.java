package com.ss_shot.ss_shot_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenshotSyncItem {

    @NotBlank(message = "Local ID is required")
    private String localId;

    private String fullText;

    private List<OcrBlock> blocks;

    @NotNull(message = "Captured timestamp is required")
    private LocalDateTime capturedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OcrBlock {
        private String text;
        private Rect rect;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Rect {
        private int x;
        private int y;
        private int width;
        private int height;
    }
}
