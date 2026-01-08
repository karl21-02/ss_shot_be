package com.ss_shot.ss_shot_be.security.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ss_shot.ss_shot_be.exception.BusinessException;
import com.ss_shot.ss_shot_be.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public OAuthUserInfo verify(String idToken) {
        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new BusinessException(ErrorCode.AUTH_004, "Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            return OAuthUserInfo.builder()
                    .email(payload.getEmail())
                    .providerId(payload.getSubject())
                    .name((String) payload.get("name"))
                    .profileUrl((String) payload.get("picture"))
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify Google ID token", e);
            throw new BusinessException(ErrorCode.AUTH_004, "Failed to verify Google ID token: " + e.getMessage());
        }
    }
}
