package com.ss_shot.ss_shot_be.controller;

import com.ss_shot.ss_shot_be.dto.request.ScreenshotUpdateRequest;
import com.ss_shot.ss_shot_be.dto.response.ScreenshotListResponse;
import com.ss_shot.ss_shot_be.dto.response.ScreenshotUpdateResponse;
import com.ss_shot.ss_shot_be.dto.response.SearchResponse;
import com.ss_shot.ss_shot_be.security.CurrentUser;
import com.ss_shot.ss_shot_be.security.UserPrincipal;
import com.ss_shot.ss_shot_be.service.ScreenshotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screenshots")
@RequiredArgsConstructor
@Tag(name = "Screenshots", description = "스크린샷 조회/검색/수정 API")
@SecurityRequirement(name = "bearerAuth")
public class ScreenshotController {

    private final ScreenshotService screenshotService;

    @GetMapping
    @Operation(summary = "스크린샷 목록 조회", description = "사용자의 스크린샷 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<ScreenshotListResponse> getScreenshots(
            @CurrentUser UserPrincipal user,
            @Parameter(description = "카테고리 필터 (FINANCE, SHOPPING, SCHEDULE, HUMOR, OTHER)")
            @RequestParam(required = false) String category,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본: 30, 최대: 100)")
            @RequestParam(defaultValue = "30") int size,
            @Parameter(description = "정렬 기준 (예: capturedAt,desc)")
            @RequestParam(defaultValue = "capturedAt,desc") String sort) {

        ScreenshotListResponse response = screenshotService.getScreenshots(
                user.getId(), category, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "스크린샷 검색", description = "텍스트로 스크린샷을 검색합니다. (최소 2자)")
    public ResponseEntity<SearchResponse> search(
            @CurrentUser UserPrincipal user,
            @Parameter(description = "검색어 (최소 2자)", required = true)
            @RequestParam String q,
            @Parameter(description = "카테고리 필터")
            @RequestParam(required = false) String category,
            @Parameter(description = "페이지 번호")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본: 50)")
            @RequestParam(defaultValue = "50") int size) {

        SearchResponse response = screenshotService.search(
                user.getId(), q, category, page, size);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "스크린샷 수정", description = "스크린샷의 삭제 상태 또는 즐겨찾기 상태를 수정합니다.")
    public ResponseEntity<ScreenshotUpdateResponse> updateScreenshot(
            @CurrentUser UserPrincipal user,
            @Parameter(description = "스크린샷 ID", required = true)
            @PathVariable Long id,
            @RequestBody ScreenshotUpdateRequest request) {

        ScreenshotUpdateResponse response = screenshotService.updateScreenshot(
                user.getId(), id, request);
        return ResponseEntity.ok(response);
    }
}
