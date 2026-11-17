package com.helixion.sarkariPariksha.Config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@Getter
public class RazorpayConfig {

    @Value("${razorpay.key.id:rzp_test_1DP5mmOlF5G5ag}")
    private String keyId;

    @Value("${razorpay.key.secret:s1sXw1YQ9XoV0Yq8R3N4k5M6}")
    private String keySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    @Value("${razorpay.company.name:Sarkari Pariksha}")
    private String companyName;

    @Value("${razorpay.webhook.secret:}")
    private String webhookSecret;

    @Bean
    public RazorpayClient razorpayClient() {
        try {
            log.info("Initializing RazorpayClient with Key ID: {}", keyId);
            return new RazorpayClient(keyId, keySecret);
        } catch (RazorpayException e) {
            log.error("Failed to initialize RazorpayClient: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize Razorpay client", e);
        }
    }
}

