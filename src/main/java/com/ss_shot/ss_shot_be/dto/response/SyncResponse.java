package com.ss_shot.ss_shot_be.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncResponse {

    private int synced;

    private int failed;

    private List<SyncResultItem> results;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SyncResultItem {
        private String localId;
        private Long serverId;
        private String category;
        private String status;  // SUCCESS or FAILED
    }
}
