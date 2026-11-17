package com.helixion.sarkariPariksha.Controller;

import com.helixion.sarkariPariksha.Request.MobileLoginRequest;
import com.helixion.sarkariPariksha.Request.RefreshTokenRequest;
import com.helixion.sarkariPariksha.Response.AuthResponse;
import com.helixion.sarkariPariksha.Services.AuthServices;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Auth")
@Tag(name = "Authentication Controller", description = "APIs for Mobile Login (MSG91) and JWT Token Refresh")
public class AuthController {

    @Autowired
    private AuthServices authServices;

    @PostMapping("/login")
    @Operation(
            summary = "Login / Register with Mobile Number & MSG91 Token",
            description = "Frontend me MSG91 se OTP verify hone ke baad mobileNumber aur msg91Token bhej kar JWT session token generate karta hai."
    )
    public BaseResponse<AuthResponse> login(@RequestBody MobileLoginRequest request) {
        return authServices.login(request);
    }

    @PostMapping("/refreshToken")
    @Operation(
            summary = "Refresh JWT Access Token",
            description = "Existing valid token bhej kar naya fresh JWT Bearer token generate karta hai."
    )
    public BaseResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return authServices.refreshToken(request);
    }
}
