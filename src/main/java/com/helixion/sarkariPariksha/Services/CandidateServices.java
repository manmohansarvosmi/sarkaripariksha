package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Entity.CandidateProfile;
import com.helixion.sarkariPariksha.Request.CandidatesRequest;
import com.helixion.sarkariPariksha.Response.CandidateProfileResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;

public interface CandidateServices {
    BaseResponse<CandidateProfile> saveCandidatesProfile(CandidatesRequest candidatesRequest);
    BaseResponse<CandidateProfileResponse> getCandidateProfileById(Long id);
}
