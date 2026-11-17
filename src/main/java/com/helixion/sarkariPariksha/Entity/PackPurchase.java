package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "pack_purchase")
@Data
public class PackPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Internal unique order/receipt number (e.g. SP_ORD_1731500000000)
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    // Razorpay Order ID (e.g. order_EKwxwAgituvdGA)
    @Column(name = "razorpay_order_id", unique = true)
    private String razorpayOrderId;

    // Razorpay Payment ID (e.g. pay_29QQoUBi66xm2f)
    @Column(name = "payment_id")
    private String paymentId;

    // Razorpay Signature for verification
    @Column(name = "razorpay_signature", length = 500)
    private String razorpaySignature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    // Nullable if user purchased a specific TestSeries
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", nullable = true)
    private PackMaster pack;

    // Nullable if user purchased a full subscription pack
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_series_id", nullable = true)
    private TestSeries testSeries;

    @Column(name = "purchase_amount")
    private BigDecimal purchaseAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "currency", length = 10)
    private String currency = "INR";

    @Column(name = "payment_gateway")
    private String paymentGateway = "RAZORPAY";

    // CREATED, PENDING, SUCCESS, FAILED, REFUNDED
    @Column(name = "payment_status")
    private String paymentStatus = "CREATED";

    @Column(name = "receipt")
    private String receipt;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        if (this.paymentGateway == null) {
            this.paymentGateway = "RAZORPAY";
        }
        if (this.currency == null) {
            this.currency = "INR";
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = "CREATED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
