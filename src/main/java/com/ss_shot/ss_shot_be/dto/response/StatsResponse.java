package com.ss_shot.ss_shot_be.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsResponse {

    private long total;

    private Map<String, Long> categories;

    private ThisMonth thisMonth;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ThisMonth {
        private long added;
        private long deleted;
    }
}
