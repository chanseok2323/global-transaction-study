package com.chanseok.payment.dto;

import java.io.Serializable;

public class PaymentDto implements Serializable {
    private String paymentId;
    private Integer amount;
    private Integer price;

    public PaymentDto(String paymentId, Integer amount, Integer price) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.price = price;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getPrice() {
        return price;
    }
}
