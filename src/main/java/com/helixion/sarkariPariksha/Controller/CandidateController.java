package com.helixion.sarkariPariksha.Controller;

import com.helixion.sarkariPariksha.Entity.CandidateProfile;
import com.helixion.sarkariPariksha.Request.CandidatesRequest;
import com.helixion.sarkariPariksha.Response.CandidateProfileResponse;
import com.helixion.sarkariPariksha.Services.CandidateServices;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Candidate")
@Tag(name = "Candidate Controller", description = "APIs for Candidate profile and information management")
public class CandidateController {

    @Autowired
    private CandidateServices candidateServices;

    @PostMapping("/saveProfile")
    @Operation(summary = "Save or Update Candidate Profile", description = "Saves candidate personal, educational, and location details, and associates them with a user account.")
    public BaseResponse<CandidateProfile> saveCandidatesProfile(@RequestBody CandidatesRequest candidatesRequest) {
        return candidateServices.saveCandidatesProfile(candidatesRequest);
    }

    @GetMapping("/getProfileById/{id}")
    @Operation(summary = "Get Candidate Profile By ID", description = "Fetches candidate profile details by candidate ID")
    public BaseResponse<CandidateProfileResponse> getCandidateProfileById(@PathVariable Long id) {
        return candidateServices.getCandidateProfileById(id);
    }
}
