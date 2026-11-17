package com.helixion.sarkariPariksha.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response returned when a Razorpay order is created, containing checkout configuration")
public class RazorpayOrderResponse {

    @Schema(description = "Internal database purchase record ID", example = "10")
    private Long purchaseId;

    @Schema(description = "Internal unique order / receipt number", example = "SP_ORD_1731500123456_8291")
    private String orderNumber;

    @Schema(description = "Razorpay Order ID to pass to Razorpay Checkout", example = "order_OIdzN85NnB9H6F")
    private String razorpayOrderId;

    @Schema(description = "Order amount in INR (Rupees)", example = "299.00")
    private BigDecimal amount;

    @Schema(description = "Order amount in paise for Razorpay mobile/web checkout (e.g. 29900)", example = "29900")
    private Long amountInPaise;

    @Schema(description = "Currency code", example = "INR")
    private String currency;

    @Schema(description = "Public Razorpay Key ID for client checkout", example = "rzp_test_xxxxxx")
    private String razorpayKeyId;

    @Schema(description = "Business name shown in Razorpay checkout modal", example = "Sarkari Pariksha")
    private String companyName;

    @Schema(description = "Pack ID if a pack was purchased", example = "1")
    private Long packId;

    @Schema(description = "Pack name", example = "SSC Master All-in-One Pack")
    private String packName;

    @Schema(description = "Test Series ID if a test series was purchased", example = "2")
    private Long testSeriesId;

    @Schema(description = "Test Series title", example = "SSC CGL 2025 Tier-1 & Tier-2 Master Test Series")
    private String testSeriesTitle;

    @Schema(description = "Candidate Profile ID", example = "1")
    private Long candidateId;

    @Schema(description = "Candidate full name for prefill", example = "Rahul Sharma")
    private String candidateName;

    @Schema(description = "Candidate email address for prefill", example = "rahul@example.com")
    private String candidateEmail;

    @Schema(description = "Candidate mobile number for prefill", example = "9876543210")
    private String candidatePhone;

    @Schema(description = "Current payment status", example = "CREATED")
    private String status;

    @Schema(description = "Order creation timestamp")
    private Date createdAt;
}

