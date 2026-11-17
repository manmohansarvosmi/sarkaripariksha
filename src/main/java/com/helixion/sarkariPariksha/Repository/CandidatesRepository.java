package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidatesRepository extends JpaRepository<CandidateProfile, Long> {

    Optional<CandidateProfile> findByUserId(Long userId);

    Optional<CandidateProfile> findByUser_MobileNumber(String mobileNumber);
}
