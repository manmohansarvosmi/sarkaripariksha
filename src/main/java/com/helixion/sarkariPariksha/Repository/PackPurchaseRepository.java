package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.PackPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackPurchaseRepository extends JpaRepository<PackPurchase, Long> {

    Optional<PackPurchase> findByRazorpayOrderId(String razorpayOrderId);

    Optional<PackPurchase> findByOrderNumber(String orderNumber);

    Optional<PackPurchase> findByPaymentId(String paymentId);

    List<PackPurchase> findByCandidate_IdOrderByCreatedAtDesc(Long candidateId);

    List<PackPurchase> findAllByOrderByCreatedAtDesc();

    List<PackPurchase> findByPaymentStatus(String paymentStatus);
}

