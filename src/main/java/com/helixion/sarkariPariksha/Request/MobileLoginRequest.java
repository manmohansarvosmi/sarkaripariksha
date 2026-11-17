package com.helixion.sarkariPariksha.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to authenticate candidate using mobile number and MSG91 token from frontend")
public class MobileLoginRequest {

    @Schema(description = "Candidate 10-digit mobile number", example = "9876543210", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mobileNumber;

    @Schema(description = "MSG91 verification token / access token from frontend", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String msg91Token;
}
