package com.ss_shot.ss_shot_be.service;

import com.ss_shot.ss_shot_be.dto.request.AppleAuthRequest;
import com.ss_shot.ss_shot_be.dto.request.GoogleAuthRequest;
import com.ss_shot.ss_shot_be.dto.request.RefreshTokenRequest;
import com.ss_shot.ss_shot_be.dto.response.AuthResponse;
import com.ss_shot.ss_shot_be.dto.response.TokenRefreshResponse;
import com.ss_shot.ss_shot_be.entity.AuthProvider;
import com.ss_shot.ss_shot_be.entity.RefreshToken;
import com.ss_shot.ss_shot_be.entity.User;
import com.ss_shot.ss_shot_be.exception.BusinessException;
import com.ss_shot.ss_shot_be.exception.ErrorCode;
import com.ss_shot.ss_shot_be.repository.RefreshTokenRepository;
import com.ss_shot.ss_shot_be.repository.UserRepository;
import com.ss_shot.ss_shot_be.security.JwtTokenProvider;
import com.ss_shot.ss_shot_be.security.oauth.AppleTokenVerifier;
import com.ss_shot.ss_shot_be.security.oauth.GoogleTokenVerifier;
import com.ss_shot.ss_shot_be.security.oauth.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final AppleTokenVerifier appleTokenVerifier;

    @Transactional
    public AuthResponse googleLogin(GoogleAuthRequest request) {
        OAuthUserInfo userInfo = googleTokenVerifier.verify(request.getIdToken());
        return processLogin(userInfo, AuthProvider.GOOGLE);
    }

    @Transactional
    public AuthResponse appleLogin(AppleAuthRequest request) {
        OAuthUserInfo userInfo = appleTokenVerifier.verify(request.getIdentityToken(), request.getName());
        return processLogin(userInfo, AuthProvider.APPLE);
    }

    @Transactional
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_003, "Invalid refresh token"));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new BusinessException(ErrorCode.AUTH_002, "Refresh token has expired");
        }

        User user = storedToken.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .build();
    }

    private AuthResponse processLogin(OAuthUserInfo userInfo, AuthProvider provider) {
        // 기존 유저 찾기 또는 새로 생성
        User user = userRepository.findByProviderAndProviderId(provider, userInfo.getProviderId())
                .orElseGet(() -> createUser(userInfo, provider));

        // 기존 유저라면 정보 업데이트
        if (user.getId() != null) {
            updateUserInfo(user, userInfo);
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .profileUrl(user.getProfileUrl())
                        .build())
                .build();
    }

    private User createUser(OAuthUserInfo userInfo, AuthProvider provider) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .provider(provider)
                .providerId(userInfo.getProviderId())
                .name(userInfo.getName())
                .profileUrl(userInfo.getProfileUrl())
                .build();
        return userRepository.save(user);
    }

    private void updateUserInfo(User user, OAuthUserInfo userInfo) {
        boolean updated = false;

        if (userInfo.getName() != null && !userInfo.getName().equals(user.getName())) {
            user.setName(userInfo.getName());
            updated = true;
        }

        if (userInfo.getProfileUrl() != null && !userInfo.getProfileUrl().equals(user.getProfileUrl())) {
            user.setProfileUrl(userInfo.getProfileUrl());
            updated = true;
        }

        if (updated) {
            userRepository.save(user);
        }
    }

    private RefreshToken createRefreshToken(User user) {
        String token = jwtTokenProvider.generateRefreshToken();
        long expirationMillis = jwtTokenProvider.getRefreshTokenExpirationInMillis();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(expirationMillis / 1000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }
}
