package com.ss_shot.ss_shot_be.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenshotUpdateRequest {

    private Boolean isDeleted;

    private Boolean isFavorite;
}
