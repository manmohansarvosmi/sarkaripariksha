package com.helixion.sarkariPariksha.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Payment history / transaction details response")
public class PaymentHistoryResponse {

    @Schema(description = "Purchase Record ID", example = "10")
    private Long id;

    @Schema(description = "Order / Receipt Number", example = "SP_ORD_1731500123456_8291")
    private String orderNumber;

    @Schema(description = "Razorpay Order ID", example = "order_OIdzN85NnB9H6F")
    private String razorpayOrderId;

    @Schema(description = "Razorpay Payment ID", example = "pay_OIdzvbXZN5f3J1")
    private String paymentId;

    @Schema(description = "Payment Gateway", example = "RAZORPAY")
    private String paymentGateway;

    @Schema(description = "Payment Status", example = "SUCCESS")
    private String paymentStatus;

    @Schema(description = "Original purchase price", example = "499.00")
    private BigDecimal purchaseAmount;

    @Schema(description = "Discount applied", example = "200.00")
    private BigDecimal discountAmount;

    @Schema(description = "Final paid amount", example = "299.00")
    private BigDecimal paidAmount;

    @Schema(description = "Currency", example = "INR")
    private String currency;

    @Schema(description = "Candidate Profile ID", example = "1")
    private Long candidateId;

    @Schema(description = "Candidate Name", example = "Rahul Sharma")
    private String candidateName;

    @Schema(description = "Candidate Mobile Number", example = "9876543210")
    private String candidateMobile;

    @Schema(description = "Pack ID if pack purchased", example = "1")
    private Long packId;

    @Schema(description = "Pack Name", example = "SSC All-in-One Pack")
    private String packName;

    @Schema(description = "Test Series ID if test series purchased", example = "2")
    private Long testSeriesId;

    @Schema(description = "Test Series Title", example = "SSC CGL 2025 Tier-1 & Tier-2 Master Test Series")
    private String testSeriesTitle;

    @Schema(description = "Payment completion date")
    private Date purchaseDate;

    @Schema(description = "Order creation timestamp")
    private Date createdAt;

    @Schema(description = "Failure reason if payment failed", example = "Signature verification failed")
    private String failureReason;

    public void setReceipt(String receipt) {
    }
}

