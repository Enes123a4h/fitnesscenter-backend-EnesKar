package at.htl.fitnesscenter.dto;

import at.htl.fitnesscenter.model.PaymentMethod;
import at.htl.fitnesscenter.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentResponse {

    private Long id;
    private Long memberId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
