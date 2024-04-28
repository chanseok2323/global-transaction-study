package com.chanseok.order.dto;

import com.chanseok.order.entity.Order;
import com.chanseok.order.entity.OrderState;

import java.math.BigDecimal;

public class OrderDto {

    public static class Request {
        private Long no;
        private String name;
        private BigDecimal price;
        private Integer quantity;

        public Request(String name, BigDecimal price, Integer quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public Long getNo() {
            return no;
        }

        public void setNo(Long no) {
            this.no = no;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Order toEntity() {
            return new Order(name, OrderState.ORDERING, price, quantity);
        }
    }

    public static class Response {
        private Long no;
        private String name;
        private OrderState orderState;
        private BigDecimal price;
        private Integer quantity;

        public Response(Long no, String name, OrderState orderState, BigDecimal price, Integer quantity) {
            this.no = no;
            this.name = name;
            this.orderState = orderState;
            this.price = price;
            this.quantity = quantity;
        }

        public Long getNo() {
            return no;
        }

        public void setNo(Long no) {
            this.no = no;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public OrderState getOrderState() {
            return orderState;
        }

        public void setOrderState(OrderState orderState) {
            this.orderState = orderState;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
