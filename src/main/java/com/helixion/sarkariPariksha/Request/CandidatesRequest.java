package com.helixion.sarkariPariksha.Request;

import lombok.Data;

import java.util.Date;

@Data
public class CandidatesRequest {
    private Long id;

    private Long userId;

    private String fullName;

    private Date dateOfBirth;

    private String gender;

    private String email;

    private String profilePhoto;

    private Boolean mobileVerified ;

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
}
