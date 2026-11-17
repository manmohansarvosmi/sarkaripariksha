package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Request.CreateOrderRequest;
import com.helixion.sarkariPariksha.Request.PaymentVerificationRequest;
import com.helixion.sarkariPariksha.Response.PaymentHistoryResponse;
import com.helixion.sarkariPariksha.Response.PaymentVerificationResponse;
import com.helixion.sarkariPariksha.Response.RazorpayOrderResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;

public interface PaymentServices {

    BaseResponse<RazorpayOrderResponse> createOrder(CreateOrderRequest request);

    BaseResponse<PaymentVerificationResponse> verifyPayment(PaymentVerificationRequest request);

    BaseResponse<PaymentHistoryResponse> getPaymentById(Long id);

    BaseResponse<PaymentHistoryResponse> getPaymentByRazorpayOrderId(String razorpayOrderId);

    BaseResponse<PaymentHistoryResponse> getPaymentsByCandidateId(Long candidateId);

    BaseResponse<PaymentHistoryResponse> getAllPayments();

    BaseResponse<String> handleWebhook(String payload, String signature);
}

