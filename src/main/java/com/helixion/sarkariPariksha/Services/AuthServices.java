package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Request.MobileLoginRequest;
import com.helixion.sarkariPariksha.Request.RefreshTokenRequest;
import com.helixion.sarkariPariksha.Response.AuthResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;

public interface AuthServices {

    BaseResponse<AuthResponse> login(MobileLoginRequest request);

    BaseResponse<AuthResponse> refreshToken(RefreshTokenRequest request);
}
