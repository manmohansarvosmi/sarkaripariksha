package com.helixion.sarkariPariksha.Controller;

import com.helixion.sarkariPariksha.Request.CreateOrderRequest;
import com.helixion.sarkariPariksha.Request.PaymentVerificationRequest;
import com.helixion.sarkariPariksha.Response.PaymentHistoryResponse;
import com.helixion.sarkariPariksha.Response.PaymentVerificationResponse;
import com.helixion.sarkariPariksha.Response.RazorpayOrderResponse;
import com.helixion.sarkariPariksha.Services.PaymentServices;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Payment")
@Tag(name = "Payment Controller", description = "APIs for Razorpay Order Creation, Signature Verification, Webhooks, and Purchase History")
public class PaymentController {

    @Autowired
    private PaymentServices paymentServices;

    @PostMapping("/createOrder")
    @Operation(
            summary = "Create Razorpay Order",
            description = "Creates a Razorpay order for purchasing a test series or subscription pack. Returns orderId, amount in paise, and prefill details for client checkout."
    )
    public BaseResponse<RazorpayOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        return paymentServices.createOrder(request);
    }

    @PostMapping("/verifyPayment")
    @Operation(
            summary = "Verify Razorpay Payment",
            description = "Verifies the HMAC SHA-256 signature returned by Razorpay Checkout. Upon verification, marks order as SUCCESS and activates candidate license."
    )
    public BaseResponse<PaymentVerificationResponse> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        return paymentServices.verifyPayment(request);
    }

    @PostMapping("/webhook")
    @Operation(
            summary = "Razorpay Webhook Handler",
            description = "Handles Razorpay webhook events (order.paid, payment.captured, payment.failed) for automated server-side verification."
    )
    public BaseResponse<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature
    ) {
        return paymentServices.handleWebhook(payload, signature);
    }

    @GetMapping("/getPaymentById/{id}")
    @Operation(summary = "Get Payment Details by Internal ID", description = "Fetches purchase record details by primary key ID.")
    public BaseResponse<PaymentHistoryResponse> getPaymentById(@PathVariable Long id) {
        return paymentServices.getPaymentById(id);
    }

    @GetMapping("/getPaymentByRazorpayOrderId/{razorpayOrderId}")
    @Operation(summary = "Get Payment Details by Razorpay Order ID", description = "Fetches purchase record details by Razorpay order ID (e.g. order_xxx).")
    public BaseResponse<PaymentHistoryResponse> getPaymentByRazorpayOrderId(@PathVariable String razorpayOrderId) {
        return paymentServices.getPaymentByRazorpayOrderId(razorpayOrderId);
    }

    @GetMapping("/getPaymentsByCandidate/{candidateId}")
    @Operation(summary = "Get Payment History by Candidate ID", description = "Fetches all purchase/payment transactions of a candidate.")
    public BaseResponse<PaymentHistoryResponse> getPaymentsByCandidateId(@PathVariable Long candidateId) {
        return paymentServices.getPaymentsByCandidateId(candidateId);
    }

    @GetMapping("/getAllPayments")
    @Operation(summary = "Get All Payments (Admin)", description = "Fetches all payment transactions in descending order of creation.")
    public BaseResponse<PaymentHistoryResponse> getAllPayments() {
        return paymentServices.getAllPayments();
    }
}

