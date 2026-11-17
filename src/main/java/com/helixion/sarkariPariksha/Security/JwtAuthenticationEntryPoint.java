package com.helixion.sarkariPariksha.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized access error for URI {}: {}", request.getRequestURI(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(0);
        baseResponse.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        baseResponse.setMessage("Unauthorized: Access token is missing or invalid. Please login with mobile number.");
        baseResponse.setData(null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), baseResponse);
    }
}

