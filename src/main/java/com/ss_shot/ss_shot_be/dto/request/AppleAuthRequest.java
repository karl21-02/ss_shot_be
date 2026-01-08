package com.ss_shot.ss_shot_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppleAuthRequest {

    @NotBlank(message = "Identity token is required")
    private String identityToken;

    @NotBlank(message = "Authorization code is required")
    private String authorizationCode;

    private String name;
}
