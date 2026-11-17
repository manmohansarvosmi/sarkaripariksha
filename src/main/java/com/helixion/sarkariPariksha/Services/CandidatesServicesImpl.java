package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Entity.CandidateProfile;
import com.helixion.sarkariPariksha.Entity.Users;
import com.helixion.sarkariPariksha.Repository.CandidatesRepository;
import com.helixion.sarkariPariksha.Repository.UsersRepository;
import com.helixion.sarkariPariksha.Request.CandidatesRequest;
import com.helixion.sarkariPariksha.Response.CandidateProfileResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class CandidatesServicesImpl implements CandidateServices {

    @Autowired
    private CandidatesRepository candidatesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public BaseResponse<CandidateProfile> saveCandidatesProfile(CandidatesRequest candidatesRequest) {
        BaseResponse<CandidateProfile> baseResponse = new BaseResponse<>();
        try {
            if (candidatesRequest == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Request cannot be null");
                return baseResponse;
            }
            CandidateProfile candidateProfile = null;

            // Check if profile exists by ID or by User ID (to update existing profile)
            if (candidatesRequest.getId() != null) {
                candidateProfile = candidatesRepository.findById(candidatesRequest.getId()).orElse(null);
            } else if (candidatesRequest.getUserId() != null) {
                candidateProfile = candidatesRepository.findByUserId(candidatesRequest.getUserId()).orElse(null);
            }
            // If not found, create new candidate profile
            if (candidateProfile == null) {
                candidateProfile = new CandidateProfile();
                candidateProfile.setCreatedAt(new Date());
            }
            // Validate and assign user
            if (candidatesRequest.getUserId() != null) {
                Optional<Users> userOptional = usersRepository.findById(candidatesRequest.getUserId());
                if (userOptional.isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(404);
                    baseResponse.setMessage("User not found with ID: " + candidatesRequest.getUserId());
                    return baseResponse;
                }
                candidateProfile.setUser(userOptional.get());
            }
            // user_id cannot be null in database
            if (candidateProfile.getUser() == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("User ID is required to save candidate profile");
                return baseResponse;
            }
            // Map personal info
            candidateProfile.setFullName(candidatesRequest.getFullName());
            candidateProfile.setDateOfBirth(candidatesRequest.getDateOfBirth());
            candidateProfile.setGender(candidatesRequest.getGender());
            candidateProfile.setEmail(candidatesRequest.getEmail());
            candidateProfile.setProfilePhoto(candidatesRequest.getProfilePhoto());
            candidateProfile.setMobileVerified(candidatesRequest.getMobileVerified() != null ? candidatesRequest.getMobileVerified() : false);

            // Map education info
            candidateProfile.setHighestQualification(candidatesRequest.getHighestQualification());
            candidateProfile.setDegreeStream(candidatesRequest.getDegreeStream());
            candidateProfile.setPassingYear(candidatesRequest.getPassingYear());
            candidateProfile.setDefaultLanguage(candidatesRequest.getDefaultLanguage());

            // Map location info
            candidateProfile.setState(candidatesRequest.getState());
            candidateProfile.setDistrict(candidatesRequest.getDistrict());
            candidateProfile.setCity(candidatesRequest.getCity());
            candidateProfile.setPincode(candidatesRequest.getPincode());

            // Audit timestamp
            candidateProfile.setUpdatedAt(new Date());

            CandidateProfile savedProfile = candidatesRepository.save(candidateProfile);

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Candidate profile saved successfully");
            baseResponse.setData(Collections.singletonList(savedProfile));

        } catch (Exception exception) {
            log.error("Exception in saveCandidatesProfile: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to save candidate profile: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<CandidateProfileResponse> getCandidateProfileById(Long id) {
        BaseResponse<CandidateProfileResponse> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Candidate ID cannot be null");
                return baseResponse;
            }

            Optional<CandidateProfile> candidateProfileOptional = candidatesRepository.findById(id);
            if (candidateProfileOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Candidate profile not found with ID: " + id);
                return baseResponse;
            }

            CandidateProfile profile = candidateProfileOptional.get();
            CandidateProfileResponse response = mapToCandidateProfileResponse(profile);

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Candidate profile fetched successfully");
            baseResponse.setData(Collections.singletonList(response));

        } catch (Exception exception) {
            log.error("Exception in getCandidateProfileById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch candidate profile: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    private CandidateProfileResponse mapToCandidateProfileResponse(CandidateProfile profile) {
        CandidateProfileResponse response = new CandidateProfileResponse();
        response.setId(profile.getId());
        if (profile.getUser() != null) {
            response.setUserId(profile.getUser().getId());
            response.setMobileNumber(profile.getUser().getMobileNumber());
        }
        response.setFullName(profile.getFullName());
        response.setDateOfBirth(profile.getDateOfBirth());
        response.setGender(profile.getGender());
        response.setEmail(profile.getEmail());
        response.setProfilePhoto(profile.getProfilePhoto());
        response.setMobileVerified(profile.getMobileVerified());

        // Education
        response.setHighestQualification(profile.getHighestQualification());
        response.setDegreeStream(profile.getDegreeStream());
        response.setPassingYear(profile.getPassingYear());
        response.setDefaultLanguage(profile.getDefaultLanguage());

        // Location
        response.setState(profile.getState());
        response.setDistrict(profile.getDistrict());
        response.setCity(profile.getCity());
        response.setPincode(profile.getPincode());

        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
}
