package com.ss_shot.ss_shot_be.dto.request;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncRequest {

    @Valid
    private List<ScreenshotSyncItem> screenshots;
}
