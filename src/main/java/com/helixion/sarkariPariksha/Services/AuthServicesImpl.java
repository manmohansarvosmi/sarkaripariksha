package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Entity.CandidateProfile;
import com.helixion.sarkariPariksha.Entity.Users;
import com.helixion.sarkariPariksha.Repository.CandidatesRepository;
import com.helixion.sarkariPariksha.Repository.UsersRepository;
import com.helixion.sarkariPariksha.Request.MobileLoginRequest;
import com.helixion.sarkariPariksha.Request.RefreshTokenRequest;
import com.helixion.sarkariPariksha.Response.AuthResponse;
import com.helixion.sarkariPariksha.Security.JwtUtils;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;

@Service
@Slf4j
public class AuthServicesImpl implements AuthServices {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CandidatesRepository candidatesRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @Transactional
    public BaseResponse<AuthResponse> login(MobileLoginRequest request) {
        BaseResponse<AuthResponse> baseResponse = new BaseResponse<>();
        try {
            if (request == null || request.getMobileNumber() == null || request.getMobileNumber().trim().isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Mobile number is required");
                return baseResponse;
            }

            // 1. Mobile number format clean (country code / spaces strip)
            String cleanMobile = cleanMobileNumber(request.getMobileNumber().trim());
            if (cleanMobile.length() < 10) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Invalid mobile number format. Please provide a valid 10-digit mobile number");
                return baseResponse;
            }

            log.info("Processing login request for mobile: {}", cleanMobile);

            Users user = usersRepository.findByMobileNumber(cleanMobile).orElse(null);
            CandidateProfile profile = null;
            boolean isNewUser = false;

            if (user == null) {
                // ========================================================
                // FLOW: Mobile exist nahi karta -> New User Register
                // Step 1: Create Users record
                // ========================================================
                isNewUser = true;
                user = new Users();
                user.setMobileNumber(cleanMobile);
                user.setActive(true);
                user.setRole("ROLE_CANDIDATE");
                user.setCreatedAt(new Date());
                user.setUpdatedAt(new Date());
                user = usersRepository.save(user);
                log.info("Step 1: New User created successfully with ID: {} for mobile: {}", user.getId(), cleanMobile);

                // ========================================================
                // Step 2: Create Candidate Profile for this user
                // ========================================================
                profile = new CandidateProfile();
                profile.setUser(user);
                profile.setMobileVerified(true);
                profile.setCreatedAt(new Date());
                profile.setUpdatedAt(new Date());
                profile = candidatesRepository.save(profile);
                log.info("Step 2: CandidateProfile created successfully with ID: {} for User ID: {}", profile.getId(), user.getId());

            } else {
                // ========================================================
                // FLOW: Mobile already exists -> Existing User Login
                // ========================================================
                if (user.getActive() != null && !user.getActive()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(403);
                    baseResponse.setMessage("Your account has been deactivated. Please contact support.");
                    return baseResponse;
                }

                // Existing user ka CandidateProfile fetch karo
                profile = candidatesRepository.findByUserId(user.getId()).orElse(null);
                if (profile == null) {
                    // Agar kisi wajah se profile missing thi toh create kar lo
                    profile = new CandidateProfile();
                    profile.setUser(user);
                    profile.setMobileVerified(true);
                    profile.setCreatedAt(new Date());
                    profile.setUpdatedAt(new Date());
                    profile = candidatesRepository.save(profile);
                    log.info("CandidateProfile created for existing User ID: {}", user.getId());
                } else {
                    profile.setMobileVerified(true);
                    profile.setUpdatedAt(new Date());
                    profile = candidatesRepository.save(profile);
                }
                log.info("Existing user login verified for User ID: {}, Candidate ID: {}", user.getId(), profile.getId());
            }

            // ============================================================
            // Step 3: Generate JWT Token (containing userId & candidateId)
            // ============================================================
            String jwtToken = jwtUtils.generateToken(
                    user.getMobileNumber(),
                    user.getId(),
                    profile.getId(),
                    user.getRole()
            );

            boolean isProfileCompleted = profile.getEmail() != null && !profile.getEmail().trim().isEmpty()
                    && profile.getHighestQualification() != null && !profile.getHighestQualification().trim().isEmpty();

            // Build AuthResponse
            AuthResponse response = new AuthResponse();
            response.setToken(jwtToken);
            response.setUserId(user.getId());
            response.setCandidateId(profile.getId());
            response.setMobileNumber(user.getMobileNumber());

            response.setProfileCompleted(isProfileCompleted);

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage(isNewUser
                    ? "New user registered and candidate profile created successfully"
                    : "Login successful! Welcome back.");
            baseResponse.setData(Collections.singletonList(response));
            return baseResponse;

        } catch (Exception e) {
            log.error("Error during mobile login / registration: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Login failed: " + e.getMessage());
            return baseResponse;
        }
    }

    @Override
    public BaseResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
        BaseResponse<AuthResponse> baseResponse = new BaseResponse<>();
        try {
            if (request == null || request.getToken() == null || request.getToken().trim().isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Token is required to refresh");
                return baseResponse;
            }

            String oldToken = request.getToken().trim();
            if (oldToken.startsWith("Bearer ")) {
                oldToken = oldToken.substring(7);
            }

            if (!jwtUtils.validateToken(oldToken)) {
                baseResponse.setStatus(0);
                baseResponse.setCode(401);
                baseResponse.setMessage("Invalid or expired JWT token");
                return baseResponse;
            }

            String mobileNumber = jwtUtils.getMobileNumberFromToken(oldToken);
            Long userId = jwtUtils.getUserIdFromToken(oldToken);
            Long candidateId = jwtUtils.getCandidateIdFromToken(oldToken);
            String role = jwtUtils.getRoleFromToken(oldToken);

            String newToken = jwtUtils.generateToken(mobileNumber, userId, candidateId, role);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(newToken);

            authResponse.setUserId(userId);
            authResponse.setCandidateId(candidateId);
            authResponse.setMobileNumber(mobileNumber);


            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Token refreshed successfully");
            baseResponse.setData(Collections.singletonList(authResponse));
            return baseResponse;

        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to refresh token: " + e.getMessage());
            return baseResponse;
        }
    }

    private String cleanMobileNumber(String raw) {
        if (raw == null) return "";
        String cleaned = raw.replaceAll("[^0-9]", "");
        if (cleaned.length() == 12 && cleaned.startsWith("91")) {
            return cleaned.substring(2);
        }
        if (cleaned.length() == 11 && cleaned.startsWith("0")) {
            return cleaned.substring(1);
        }
        return cleaned;
    }
}
