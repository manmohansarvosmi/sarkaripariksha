package com.helixion.sarkariPariksha.Response;

import lombok.Data;

import java.util.Date;

@Data
public class CandidateProfileResponse {
    private Long id;

    private Long userId;

    private String mobileNumber;

    private String fullName;

    private Date dateOfBirth;

    private String gender;

    private String email;

    private String profilePhoto;

    private Boolean mobileVerified = false;

    // Education
    private String highestQualification;

    private String degreeStream;

    private Integer passingYear;

    private String defaultLanguage;

    // Location
    private String state;
    private String district;
    private String city;
    private String pincode;

    private Date createdAt;
    private Date updatedAt;
}
