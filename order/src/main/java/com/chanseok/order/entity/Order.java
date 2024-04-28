package com.chanseok.order.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    private String name;
    private OrderState orderState;
    private BigDecimal price;
    private Integer quantity;

    public Order() {
    }

    public Order(String name, OrderState orderState, BigDecimal price, Integer quantity) {
        this.name = name;
        this.orderState = orderState;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}