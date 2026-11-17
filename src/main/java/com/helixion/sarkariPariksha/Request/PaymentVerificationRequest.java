package com.helixion.sarkariPariksha.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to verify Razorpay payment signature after successful client checkout")
public class PaymentVerificationRequest {

    @Schema(description = "Razorpay Order ID returned during order creation", example = "order_OIdzN85NnB9H6F", requiredMode = Schema.RequiredMode.REQUIRED)
    private String razorpayOrderId;

    @Schema(description = "Razorpay Payment ID returned upon successful checkout", example = "pay_OIdzvbXZN5f3J1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String razorpayPaymentId;

    @Schema(description = "Razorpay Signature returned for HMAC SHA-256 verification", example = "f6f4c4a6b0c20a...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String razorpaySignature;

    @Schema(description = "Optional candidate ID for extra security check", example = "1")
    private Long candidateId;
}

