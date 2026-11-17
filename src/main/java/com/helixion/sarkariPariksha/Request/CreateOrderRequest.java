package com.helixion.sarkariPariksha.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request to initiate Razorpay order for pack or test series purchase")
public class CreateOrderRequest {

    @Schema(description = "Candidate Profile ID making the purchase", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long candidateId;

    @Schema(description = "Pack ID if purchasing a subscription pack (either packId or testSeriesId required)", example = "1")
    private Long packId;

    @Schema(description = "Test Series ID if purchasing an individual test series pack", example = "1")
    private Long testSeriesId;

    @Schema(description = "Optional override amount in INR (if not provided, database price is used)", example = "299.00")
    private BigDecimal amount;

    @Schema(description = "Currency code (default INR)", example = "INR")
    private String currency = "INR";

    @Schema(description = "Optional custom notes / remarks for the order", example = "Purchased SSC CGL 2025 Test Series from Android App")
    private String notes;
}

