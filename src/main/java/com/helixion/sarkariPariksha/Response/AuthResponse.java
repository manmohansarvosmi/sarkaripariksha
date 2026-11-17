package com.helixion.sarkariPariksha.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Authentication response containing JWT token and candidate profile summary")
public class AuthResponse {

    @Schema(description = "JWT Bearer access token for authorizing subsequent requests", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "User primary ID in users table", example = "1")
    private Long userId;

    @Schema(description = "Candidate Profile ID", example = "1")
    private Long candidateId;

    @Schema(description = "Mobile number", example = "9876543210")
    private String mobileNumber;

    @Schema(description = "Flag indicating if candidate has filled profile details (email, education, etc.)", example = "true")
    private Boolean profileCompleted;


}

