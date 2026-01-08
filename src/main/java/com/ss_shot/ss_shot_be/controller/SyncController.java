package com.ss_shot.ss_shot_be.controller;

import com.ss_shot.ss_shot_be.dto.request.SyncRequest;
import com.ss_shot.ss_shot_be.dto.response.SyncResponse;
import com.ss_shot.ss_shot_be.security.CurrentUser;
import com.ss_shot.ss_shot_be.security.UserPrincipal;
import com.ss_shot.ss_shot_be.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Tag(name = "Sync", description = "스크린샷 메타데이터 동기화 API")
@SecurityRequirement(name = "bearerAuth")
public class SyncController {

    private final SyncService syncService;

    @PostMapping
    @Operation(summary = "스크린샷 동기화", description = "클라이언트의 스크린샷 메타데이터를 서버에 동기화합니다.")
    public ResponseEntity<SyncResponse> sync(
            @CurrentUser UserPrincipal user,
            @Valid @RequestBody SyncRequest request) {

        SyncResponse response = syncService.sync(user.getId(), request);
        return ResponseEntity.ok(response);
    }
}
