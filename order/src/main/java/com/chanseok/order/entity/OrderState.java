package com.chanseok.order.entity;

public enum OrderState {
    ORDERING("진행중"), CANCEL("주문취소"), COMPLETED("주문완료");

    private String description;

    OrderState(String description) {
        this.description = description;
    }
}