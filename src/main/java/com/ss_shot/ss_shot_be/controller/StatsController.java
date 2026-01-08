package com.ss_shot.ss_shot_be.controller;

import com.ss_shot.ss_shot_be.dto.response.StatsResponse;
import com.ss_shot.ss_shot_be.security.CurrentUser;
import com.ss_shot.ss_shot_be.security.UserPrincipal;
import com.ss_shot.ss_shot_be.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Stats", description = "통계 API")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    @Operation(summary = "통계 조회", description = "사용자의 스크린샷 카테고리별 통계를 조회합니다.")
    public ResponseEntity<StatsResponse> getStats(@CurrentUser UserPrincipal user) {
        StatsResponse response = statsService.getStats(user.getId());
        return ResponseEntity.ok(response);
    }
}
