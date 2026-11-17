package com.helixion.sarkariPariksha.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to refresh an existing JWT token")
public class RefreshTokenRequest {

    @Schema(description = "Current JWT token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;
}

