package com.ss_shot.ss_shot_be.dto.response;

import com.ss_shot.ss_shot_be.entity.ScreenshotMetadata;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {

    private List<SearchResultItem> content;

    private String query;

    private long totalElements;

    private int page;

    private int size;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchResultItem {
        private Long id;
        private String localId;
        private String fullText;
        private String matchedText;
        private String blocks;  // JSON string
        private String category;
        private LocalDateTime capturedAt;

        public static SearchResultItem from(ScreenshotMetadata metadata, String query) {
            return SearchResultItem.builder()
                    .id(metadata.getId())
                    .localId(metadata.getLocalId())
                    .fullText(metadata.getFullText())
                    .matchedText(extractMatchedText(metadata.getFullText(), query))
                    .blocks(metadata.getOcrJson())
                    .category(metadata.getCategory().name())
                    .capturedAt(metadata.getCapturedAt())
                    .build();
        }

        private static String extractMatchedText(String fullText, String query) {
            if (fullText == null || query == null) return null;
            int idx = fullText.toLowerCase().indexOf(query.toLowerCase());
            if (idx == -1) return null;
            int start = Math.max(0, idx - 20);
            int end = Math.min(fullText.length(), idx + query.length() + 20);
            return fullText.substring(start, end);
        }
    }
}
