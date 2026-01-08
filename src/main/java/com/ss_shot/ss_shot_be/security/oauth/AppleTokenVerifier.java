package com.ss_shot.ss_shot_be.security.oauth;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ss_shot.ss_shot_be.exception.BusinessException;
import com.ss_shot.ss_shot_be.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Component
@Slf4j
public class AppleTokenVerifier {

    private final JwkProvider jwkProvider;
    private final String clientId;

    public AppleTokenVerifier(@Value("${apple.client-id}") String clientId) {
        this.clientId = clientId;
        try {
            this.jwkProvider = new JwkProviderBuilder(new URL("https://appleid.apple.com/auth/keys"))
                    .cached(true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Apple JWK provider", e);
        }
    }

    public OAuthUserInfo verify(String identityToken, String name) {
        try {
            DecodedJWT jwt = JWT.decode(identityToken);
            RSAPublicKey publicKey = (RSAPublicKey) jwkProvider
                    .get(jwt.getKeyId())
                    .getPublicKey();

            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            DecodedJWT verifiedJwt = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .withAudience(clientId)
                    .build()
                    .verify(identityToken);

            String email = verifiedJwt.getClaim("email").asString();
            String sub = verifiedJwt.getSubject();

            return OAuthUserInfo.builder()
                    .email(email)
                    .providerId(sub)
                    .name(name)  // Apple은 최초 로그인 시에만 name 제공
                    .build();

        } catch (Exception e) {
            log.error("Failed to verify Apple identity token", e);
            throw new BusinessException(ErrorCode.AUTH_004, "Failed to verify Apple identity token: " + e.getMessage());
        }
    }
}
