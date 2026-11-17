package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "candidate_profile")
@Data
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users user;

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
