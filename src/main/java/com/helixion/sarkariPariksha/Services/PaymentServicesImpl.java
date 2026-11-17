package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Config.RazorpayConfig;
import com.helixion.sarkariPariksha.Entity.*;
import com.helixion.sarkariPariksha.Repository.*;
import com.helixion.sarkariPariksha.Request.CreateOrderRequest;
import com.helixion.sarkariPariksha.Request.PaymentVerificationRequest;
import com.helixion.sarkariPariksha.Response.PaymentHistoryResponse;
import com.helixion.sarkariPariksha.Response.PaymentVerificationResponse;
import com.helixion.sarkariPariksha.Response.RazorpayOrderResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class PaymentServicesImpl implements PaymentServices {

    @Autowired
    private RazorpayConfig razorpayConfig;

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private PackPurchaseRepository packPurchaseRepository;

    @Autowired
    private CandidateLicenseRepository candidateLicenseRepository;

    @Autowired
    private CandidatesRepository candidatesRepository;

    @Autowired
    private PackMasterRepository packMasterRepository;

    @Autowired
    private TestSeriesRepository testSeriesRepository;

    @Override
    @Transactional
    public BaseResponse<RazorpayOrderResponse> createOrder(CreateOrderRequest request) {
        BaseResponse<RazorpayOrderResponse> baseResponse = new BaseResponse<>();
        try {
            if (request == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Request cannot be null");
                return baseResponse;
            }

            if (request.getCandidateId() == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Candidate ID is required");
                return baseResponse;
            }

            // Verify Candidate exists
            Optional<CandidateProfile> candidateOpt = candidatesRepository.findById(request.getCandidateId());
            if (candidateOpt.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Candidate not found with ID: " + request.getCandidateId());
                return baseResponse;
            }
            CandidateProfile candidate = candidateOpt.get();

            PackMaster pack = null;
            TestSeries testSeries = null;
            BigDecimal finalAmount = BigDecimal.ZERO;
            BigDecimal originalPrice = BigDecimal.ZERO;
            BigDecimal discount = BigDecimal.ZERO;
            String itemName = "Mock Test Subscription";

            // If packId is provided
            if (request.getPackId() != null) {
                Optional<PackMaster> packOpt = packMasterRepository.findById(request.getPackId());
                if (packOpt.isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(404);
                    baseResponse.setMessage("Pack not found with ID: " + request.getPackId());
                    return baseResponse;
                }
                pack = packOpt.get();
                itemName = pack.getPackName();
                originalPrice = pack.getPrice() != null ? pack.getPrice() : BigDecimal.ZERO;

                if (pack.getDiscountPrice() != null && pack.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {
                    finalAmount = pack.getDiscountPrice();
                    discount = originalPrice.subtract(finalAmount);
                    if (discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO;
                } else {
                    finalAmount = originalPrice;
                }
            } else if (request.getTestSeriesId() != null) {
                // If testSeriesId is provided
                Optional<TestSeries> tsOpt = testSeriesRepository.findById(request.getTestSeriesId());
                if (tsOpt.isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(404);
                    baseResponse.setMessage("Test series not found with ID: " + request.getTestSeriesId());
                    return baseResponse;
                }
                testSeries = tsOpt.get();
                itemName = testSeries.getTitle();
                originalPrice = testSeries.getPrice() != null ? testSeries.getPrice() : BigDecimal.ZERO;

                if (testSeries.getDiscountPrice() != null && testSeries.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {
                    finalAmount = testSeries.getDiscountPrice();
                    discount = originalPrice.subtract(finalAmount);
                    if (discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO;
                } else {
                    finalAmount = originalPrice;
                }
            } else {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Either packId or testSeriesId must be provided");
                return baseResponse;
            }

            // Allow custom override amount if provided
            if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                finalAmount = request.getAmount();
            }

            if (finalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Order amount must be greater than zero");
                return baseResponse;
            }

            String currency = (request.getCurrency() != null && !request.getCurrency().trim().isEmpty())
                    ? request.getCurrency().trim()
                    : razorpayConfig.getCurrency();

            // Amount in paise for Razorpay
            long amountInPaise = finalAmount.multiply(new BigDecimal("100")).longValue();

            // Generate unique receipt and order number
            String receipt = "SP_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 9000 + 1000);

            // Call Razorpay API to create Order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);

            JSONObject notes = new JSONObject();
            notes.put("candidateId", candidate.getId().toString());
            notes.put("candidateName", candidate.getFullName() != null ? candidate.getFullName() : "");
            notes.put("itemPurchased", itemName);
            if (pack != null) notes.put("packId", pack.getId().toString());
            if (testSeries != null) notes.put("testSeriesId", testSeries.getId().toString());
            if (request.getNotes() != null) notes.put("userNotes", request.getNotes());
            orderRequest.put("notes", notes);

            log.info("Creating Razorpay Order for Candidate: {}, Amount: {} {}", candidate.getId(), finalAmount, currency);
            Order rzpOrder = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = rzpOrder.get("id");

            // Save PackPurchase record in local DB
            PackPurchase purchase = new PackPurchase();
            purchase.setOrderNumber(receipt);
            purchase.setRazorpayOrderId(razorpayOrderId);
            purchase.setCandidate(candidate);
            purchase.setPack(pack);
            purchase.setTestSeries(testSeries);
            purchase.setPurchaseAmount(originalPrice);
            purchase.setDiscountAmount(discount);
            purchase.setPaidAmount(finalAmount);
            purchase.setCurrency(currency);
            purchase.setPaymentGateway("RAZORPAY");
            purchase.setPaymentStatus("CREATED");
            purchase.setReceipt(receipt);
            purchase.setCreatedAt(new Date());
            purchase.setUpdatedAt(new Date());

            PackPurchase savedPurchase = packPurchaseRepository.save(purchase);
            log.info("Saved local PackPurchase ID: {}, Razorpay Order ID: {}", savedPurchase.getId(), razorpayOrderId);

            // Prepare Response
            RazorpayOrderResponse response = new RazorpayOrderResponse();
            response.setPurchaseId(savedPurchase.getId());
            response.setOrderNumber(receipt);
            response.setRazorpayOrderId(razorpayOrderId);
            response.setAmount(finalAmount);
            response.setAmountInPaise(amountInPaise);
            response.setCurrency(currency);
            response.setRazorpayKeyId(razorpayConfig.getKeyId());
            response.setCompanyName(razorpayConfig.getCompanyName());

            if (pack != null) {
                response.setPackId(pack.getId());
                response.setPackName(pack.getPackName());
            }
            if (testSeries != null) {
                response.setTestSeriesId(testSeries.getId());
                response.setTestSeriesTitle(testSeries.getTitle());
            }

            response.setCandidateId(candidate.getId());
            response.setCandidateName(candidate.getFullName());
            response.setCandidateEmail(candidate.getEmail());
            if (candidate.getUser() != null) {
                response.setCandidatePhone(candidate.getUser().getMobileNumber());
            }
            response.setStatus("CREATED");
            response.setCreatedAt(savedPurchase.getCreatedAt());

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Razorpay order created successfully");
            baseResponse.setData(Collections.singletonList(response));
            return baseResponse;

        } catch (Exception e) {
            log.error("Error creating Razorpay order: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to create Razorpay order: " + e.getMessage());
            return baseResponse;
        }
    }

    @Override
    @Transactional
    public BaseResponse<PaymentVerificationResponse> verifyPayment(PaymentVerificationRequest request) {
        BaseResponse<PaymentVerificationResponse> baseResponse = new BaseResponse<>();
        try {
            if (request == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Verification request cannot be null");
                return baseResponse;
            }

            if (request.getRazorpayOrderId() == null || request.getRazorpayOrderId().trim().isEmpty() ||
                request.getRazorpayPaymentId() == null || request.getRazorpayPaymentId().trim().isEmpty() ||
                request.getRazorpaySignature() == null || request.getRazorpaySignature().trim().isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("razorpayOrderId, razorpayPaymentId, and razorpaySignature are all required");
                return baseResponse;
            }

            // Find purchase record by Razorpay Order ID
            Optional<PackPurchase> purchaseOpt = packPurchaseRepository.findByRazorpayOrderId(request.getRazorpayOrderId().trim());
            if (purchaseOpt.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Purchase order not found for Razorpay Order ID: " + request.getRazorpayOrderId());
                return baseResponse;
            }

            PackPurchase purchase = purchaseOpt.get();

            // If already verified
            if ("SUCCESS".equalsIgnoreCase(purchase.getPaymentStatus())) {
                log.info("Purchase ID: {} already verified as SUCCESS", purchase.getId());
                PaymentVerificationResponse res = buildVerificationSuccessResponse(purchase, "Payment was already verified successfully");
                baseResponse.setStatus(1);
                baseResponse.setCode(200);
                baseResponse.setMessage("Payment is already verified and active");
                baseResponse.setData(Collections.singletonList(res));
                return baseResponse;
            }

            // Verify HMAC-SHA256 Signature
            boolean isValidSignature = verifyHmacSha256(
                    request.getRazorpayOrderId().trim(),
                    request.getRazorpayPaymentId().trim(),
                    request.getRazorpaySignature().trim(),
                    razorpayConfig.getKeySecret()
            );

            if (!isValidSignature) {
                log.warn("Payment signature verification failed for Order: {}", request.getRazorpayOrderId());
                purchase.setPaymentStatus("FAILED");
                purchase.setFailureReason("Signature verification failed / tampered signature");
                purchase.setPaymentId(request.getRazorpayPaymentId().trim());
                purchase.setRazorpaySignature(request.getRazorpaySignature().trim());
                purchase.setUpdatedAt(new Date());
                packPurchaseRepository.save(purchase);

                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Payment signature verification failed! Payment cannot be validated.");
                return baseResponse;
            }

            // Signature is valid! Update purchase details
            purchase.setPaymentId(request.getRazorpayPaymentId().trim());
            purchase.setRazorpaySignature(request.getRazorpaySignature().trim());
            purchase.setPaymentStatus("SUCCESS");
            purchase.setPurchaseDate(new Date());
            purchase.setUpdatedAt(new Date());
            PackPurchase updatedPurchase = packPurchaseRepository.save(purchase);

            // Activate candidate license
            CandidateLicense license = activateCandidateLicense(updatedPurchase);

            // If TestSeries was purchased, increment totalEnrolled count
            if (updatedPurchase.getTestSeries() != null) {
                TestSeries ts = updatedPurchase.getTestSeries();
                Long enrolled = ts.getTotalEnrolled() != null ? ts.getTotalEnrolled() : 0L;
                ts.setTotalEnrolled(enrolled + 1);
                testSeriesRepository.save(ts);
            }

            PaymentVerificationResponse response = buildVerificationSuccessResponse(updatedPurchase, "Payment verified successfully. Your access has been activated!");
            if (license != null) {
                response.setLicenseKey(license.getLicenseKey());
                response.setLicenseStartDate(license.getStartDate());
                response.setLicenseExpiryDate(license.getExpiryDate());
                response.setTotalMockTestsAllowed(license.getTotalMockTests());
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Payment verified successfully and access activated");
            baseResponse.setData(Collections.singletonList(response));
            return baseResponse;

        } catch (Exception e) {
            log.error("Error during payment verification: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Payment verification failed: " + e.getMessage());
            return baseResponse;
        }
    }

    private CandidateLicense activateCandidateLicense(PackPurchase purchase) {
        try {
            // Check if license already exists for this purchase
            Optional<CandidateLicense> existingLic = candidateLicenseRepository.findByPurchase_Id(purchase.getId());
            if (existingLic.isPresent()) {
                return existingLic.get();
            }

            CandidateLicense license = new CandidateLicense();
            license.setPurchase(purchase);
            license.setCandidate(purchase.getCandidate());
            license.setLicenseKey("LIC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            license.setStartDate(new Date());
            license.setActivatedAt(new Date());
            license.setCreatedAt(new Date());
            license.setStatus("ACTIVE");
            license.setUsedMockTests(0);

            // Determine validity and allowed tests
            int validityDays = 365; // default 1 year
            int totalTests = 50;

            if (purchase.getPack() != null) {
                PackMaster pack = purchase.getPack();
                if (pack.getValidityDays() != null && pack.getValidityDays() > 0) {
                    validityDays = pack.getValidityDays();
                }
                if (pack.getMaxMockTests() != null && pack.getMaxMockTests() > 0) {
                    totalTests = pack.getMaxMockTests();
                }
            } else if (purchase.getTestSeries() != null) {
                TestSeries ts = purchase.getTestSeries();
                if (ts.getTotalTests() != null && ts.getTotalTests() > 0) {
                    totalTests = ts.getTotalTests();
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, validityDays);
            license.setExpiryDate(cal.getTime());
            license.setTotalMockTests(totalTests);

            return candidateLicenseRepository.save(license);
        } catch (Exception e) {
            log.error("Failed to create CandidateLicense for purchase ID: {}", purchase.getId(), e);
            return null;
        }
    }

    private PaymentVerificationResponse buildVerificationSuccessResponse(PackPurchase purchase, String message) {
        PaymentVerificationResponse response = new PaymentVerificationResponse();
        response.setPurchaseId(purchase.getId());
        response.setOrderNumber(purchase.getOrderNumber());
        response.setRazorpayOrderId(purchase.getRazorpayOrderId());
        response.setRazorpayPaymentId(purchase.getPaymentId());
        response.setPaymentStatus(purchase.getPaymentStatus());
        response.setPaidAmount(purchase.getPaidAmount());
        response.setMessage(message);

        if (purchase.getCandidate() != null) {
            response.setCandidateId(purchase.getCandidate().getId());
            response.setCandidateName(purchase.getCandidate().getFullName());
        }

        if (purchase.getTestSeries() != null) {
            response.setItemPurchased(purchase.getTestSeries().getTitle());
        } else if (purchase.getPack() != null) {
            response.setItemPurchased(purchase.getPack().getPackName());
        }
        return response;
    }

    @Override
    public BaseResponse<PaymentHistoryResponse> getPaymentById(Long id) {
        BaseResponse<PaymentHistoryResponse> baseResponse = new BaseResponse<>();
        try {
            Optional<PackPurchase> purchaseOpt = packPurchaseRepository.findById(id);
            if (purchaseOpt.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Payment record not found with ID: " + id);
                return baseResponse;
            }

            PaymentHistoryResponse res = mapToPaymentHistory(purchaseOpt.get());
            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Payment record fetched successfully");
            baseResponse.setData(Collections.singletonList(res));
            return baseResponse;
        } catch (Exception e) {
            log.error("Error fetching payment by ID: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Error fetching payment: " + e.getMessage());
            return baseResponse;
        }
    }

    @Override
    public BaseResponse<PaymentHistoryResponse> getPaymentByRazorpayOrderId(String razorpayOrderId) {
        BaseResponse<PaymentHistoryResponse> baseResponse = new BaseResponse<>();
        try {
            Optional<PackPurchase> purchaseOpt = packPurchaseRepository.findByRazorpayOrderId(razorpayOrderId);
            if (purchaseOpt.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Payment record not found for Razorpay Order ID: " + razorpayOrderId);
                return baseResponse;
            }

            PaymentHistoryResponse res = mapToPaymentHistory(purchaseOpt.get());
            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Payment record fetched successfully");
            baseResponse.setData(Collections.singletonList(res));
            return baseResponse;
        } catch (Exception e) {
            log.error("Error fetching payment by Razorpay Order ID: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Error fetching payment: " + e.getMessage());
            return baseResponse;
        }
    }

    @Override
    public BaseResponse<PaymentHistoryResponse> getPaymentsByCandidateId(Long candidateId) {
        BaseResponse<PaymentHistoryResponse> baseResponse = new BaseResponse<>();
        try {
            List<PackPurchase> purchases = packPurchaseRepository.findByCandidate_IdOrderByCreatedAtDesc(candidateId);
            List<PaymentHistoryResponse> responseList = new ArrayList<>();
            for (PackPurchase p : purchases) {
                responseList.add(mapToPaymentHistory(p));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Found " + responseList.size() + " payment records for candidate");
            baseResponse.setData(responseList);
            return baseResponse;
        } catch (Exception e) {
            log.error("Error fetching payments for candidate: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Error fetching payments: " + e.getMessage());
            return baseResponse;
        }
    }

    @Override
    public BaseResponse<PaymentHistoryResponse> getAllPayments() {
        BaseResponse<PaymentHistoryResponse> baseResponse = new BaseResponse<>();
        try {
            List<PackPurchase> purchases = packPurchaseRepository.findAllByOrderByCreatedAtDesc();
            List<PaymentHistoryResponse> responseList = new ArrayList<>();
            for (PackPurchase p : purchases) {
                responseList.add(mapToPaymentHistory(p));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Fetched all payment records successfully (" + responseList.size() + ")");
            baseResponse.setData(responseList);
            return baseResponse;
        } catch (Exception e) {
            log.error("Error fetching all payments: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Error fetching payments: " + e.getMessage());
            return baseResponse;
        }
    }

    @Override
    @Transactional
    public BaseResponse<String> handleWebhook(String payload, String signature) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            if (payload == null || payload.trim().isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Empty webhook payload");
                return baseResponse;
            }

            log.info("Received Razorpay Webhook notification");

            // Webhook signature verification if webhook secret is configured
            String webhookSecret = razorpayConfig.getWebhookSecret();
            if (webhookSecret != null && !webhookSecret.trim().isEmpty() && signature != null) {
                boolean validWebhook = verifyWebhookHmac(payload, signature, webhookSecret);
                if (!validWebhook) {
                    log.warn("Invalid webhook signature received");
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Invalid webhook signature");
                    return baseResponse;
                }
            }

            JSONObject json = new JSONObject(payload);
            String event = json.optString("event");
            log.info("Razorpay Webhook Event: {}", event);

            if ("order.paid".equals(event) || "payment.captured".equals(event)) {
                JSONObject payloadObj = json.optJSONObject("payload");
                if (payloadObj != null) {
                    JSONObject paymentEntity = payloadObj.optJSONObject("payment");
                    if (paymentEntity != null) {
                        JSONObject entity = paymentEntity.optJSONObject("entity");
                        if (entity != null) {
                            String orderId = entity.optString("order_id");
                            String paymentId = entity.optString("id");

                            Optional<PackPurchase> purchaseOpt = packPurchaseRepository.findByRazorpayOrderId(orderId);
                            if (purchaseOpt.isPresent()) {
                                PackPurchase purchase = purchaseOpt.get();
                                if (!"SUCCESS".equalsIgnoreCase(purchase.getPaymentStatus())) {
                                    purchase.setPaymentStatus("SUCCESS");
                                    purchase.setPaymentId(paymentId);
                                    purchase.setPurchaseDate(new Date());
                                    purchase.setUpdatedAt(new Date());
                                    PackPurchase saved = packPurchaseRepository.save(purchase);
                                    activateCandidateLicense(saved);
                                    log.info("Webhook successfully marked purchase ID: {} as SUCCESS", saved.getId());
                                }
                            }
                        }
                    }
                }
            } else if ("payment.failed".equals(event)) {
                JSONObject payloadObj = json.optJSONObject("payload");
                if (payloadObj != null) {
                    JSONObject paymentEntity = payloadObj.optJSONObject("payment");
                    if (paymentEntity != null) {
                        JSONObject entity = paymentEntity.optJSONObject("entity");
                        if (entity != null) {
                            String orderId = entity.optString("order_id");
                            String errorDescription = entity.optString("error_description");

                            Optional<PackPurchase> purchaseOpt = packPurchaseRepository.findByRazorpayOrderId(orderId);
                            if (purchaseOpt.isPresent()) {
                                PackPurchase purchase = purchaseOpt.get();
                                purchase.setPaymentStatus("FAILED");
                                purchase.setFailureReason(errorDescription);
                                purchase.setUpdatedAt(new Date());
                                packPurchaseRepository.save(purchase);
                                log.info("Webhook marked purchase ID: {} as FAILED", purchase.getId());
                            }
                        }
                    }
                }
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Webhook processed successfully");
            baseResponse.setData(Collections.singletonList("Webhook Event: " + event));
            return baseResponse;

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Webhook processing failed: " + e.getMessage());
            return baseResponse;
        }
    }

    private PaymentHistoryResponse mapToPaymentHistory(PackPurchase p) {
        PaymentHistoryResponse res = new PaymentHistoryResponse();
        res.setId(p.getId());
        res.setOrderNumber(p.getOrderNumber());
        res.setRazorpayOrderId(p.getRazorpayOrderId());
        res.setPaymentId(p.getPaymentId());
        res.setPaymentGateway(p.getPaymentGateway());
        res.setPaymentStatus(p.getPaymentStatus());
        res.setPurchaseAmount(p.getPurchaseAmount());
        res.setDiscountAmount(p.getDiscountAmount());
        res.setPaidAmount(p.getPaidAmount());
        res.setCurrency(p.getCurrency());
        res.setReceipt(p.getReceipt());
        res.setFailureReason(p.getFailureReason());
        res.setPurchaseDate(p.getPurchaseDate());
        res.setCreatedAt(p.getCreatedAt());

        if (p.getCandidate() != null) {
            res.setCandidateId(p.getCandidate().getId());
            res.setCandidateName(p.getCandidate().getFullName());
            if (p.getCandidate().getUser() != null) {
                res.setCandidateMobile(p.getCandidate().getUser().getMobileNumber());
            }
        }

        if (p.getPack() != null) {
            res.setPackId(p.getPack().getId());
            res.setPackName(p.getPack().getPackName());
        }

        if (p.getTestSeries() != null) {
            res.setTestSeriesId(p.getTestSeries().getId());
            res.setTestSeriesTitle(p.getTestSeries().getTitle());
        }

        return res;
    }

    /**
     * Standard Razorpay Payment Signature Verification using HMAC SHA256
     * Signature = HMAC-SHA256(order_id + "|" + payment_id, secret)
     */
    private boolean verifyHmacSha256(String orderId, String paymentId, String signature, String secret) {
        try {
            String data = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String calculatedSignature = hexString.toString();
            return calculatedSignature.equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("Error calculating HMAC-SHA256 for payment verification: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Razorpay Webhook Signature Verification
     * Signature = HMAC-SHA256(payload, webhookSecret)
     */
    private boolean verifyWebhookHmac(String payload, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("Error verifying webhook HMAC: {}", e.getMessage(), e);
            return false;
        }
    }
}

