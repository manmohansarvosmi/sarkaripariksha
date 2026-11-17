package com.helixion.sarkariPariksha.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response returned after verifying Razorpay payment signature and activating candidate access")
public class PaymentVerificationResponse {

    @Schema(description = "Internal database purchase record ID", example = "10")
    private Long purchaseId;

    @Schema(description = "Internal unique order number", example = "SP_ORD_1731500123456_8291")
    private String orderNumber;

    @Schema(description = "Razorpay Order ID", example = "order_OIdzN85NnB9H6F")
    private String razorpayOrderId;

    @Schema(description = "Razorpay Payment ID", example = "pay_OIdzvbXZN5f3J1")
    private String razorpayPaymentId;

    @Schema(description = "Payment status (SUCCESS / FAILED)", example = "SUCCESS")
    private String paymentStatus;

    @Schema(description = "Actual paid amount", example = "299.00")
    private BigDecimal paidAmount;

    @Schema(description = "Candidate Profile ID", example = "1")
    private Long candidateId;

    @Schema(description = "Candidate full name", example = "Rahul Sharma")
    private String candidateName;

    @Schema(description = "Name of pack or test series purchased", example = "SSC CGL 2025 Tier-1 & Tier-2 Master Test Series")
    private String itemPurchased;

    @Schema(description = "Generated candidate license key for access", example = "LIC-A3B91C2D-4512")
    private String licenseKey;

    @Schema(description = "License activation start date")
    private Date licenseStartDate;

    @Schema(description = "License validity expiry date")
    private Date licenseExpiryDate;

    @Schema(description = "Total mock tests allowed under this license", example = "65")
    private Integer totalMockTestsAllowed;

    @Schema(description = "User friendly status message", example = "Payment verified successfully. Your test series access has been activated!")
    private String message;
}

