package com.ss_shot.ss_shot_be.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenshotListResponse {

    private List<ScreenshotResponse> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    public static ScreenshotListResponse from(Page<ScreenshotResponse> page) {
        return ScreenshotListResponse.builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
