package com.ss_shot.ss_shot_be.security.oauth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthUserInfo {
    private String email;
    private String providerId;
    private String name;
    private String profileUrl;
}
