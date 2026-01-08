package com.ss_shot.ss_shot_be.controller;

import com.ss_shot.ss_shot_be.dto.request.AppleAuthRequest;
import com.ss_shot.ss_shot_be.dto.request.GoogleAuthRequest;
import com.ss_shot.ss_shot_be.dto.request.RefreshTokenRequest;
import com.ss_shot.ss_shot_be.dto.response.AuthResponse;
import com.ss_shot.ss_shot_be.dto.response.TokenRefreshResponse;
import com.ss_shot.ss_shot_be.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    @Operation(summary = "Google 로그인", description = "Google ID Token으로 로그인합니다.")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleAuthRequest request) {
        AuthResponse response = authService.googleLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apple")
    @Operation(summary = "Apple 로그인", description = "Apple Identity Token으로 로그인합니다.")
    public ResponseEntity<AuthResponse> appleLogin(@Valid @RequestBody AppleAuthRequest request) {
        AuthResponse response = authService.appleLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새 Access Token을 발급받습니다.")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
