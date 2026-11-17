package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.CandidateLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateLicenseRepository extends JpaRepository<CandidateLicense, Long> {

    Optional<CandidateLicense> findByPurchase_Id(Long purchaseId);

    List<CandidateLicense> findByCandidate_IdOrderByCreatedAtDesc(Long candidateId);

    Optional<CandidateLicense> findByCandidate_IdAndStatus(Long candidateId, String status);

    Optional<CandidateLicense> findByLicenseKey(String licenseKey);
}

