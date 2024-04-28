package com.chanseok.order.web;

import com.chanseok.order.dto.OrderDto;
import com.chanseok.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<OrderDto.Response> createOrder() {
        logger.info("call createOrder");
        OrderDto.Request orderRequest = new OrderDto.Request("SAGA Simple Test", BigDecimal.valueOf(10000), 1);
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @PostMapping("/order/cancel")
    public ResponseEntity<Long> cancelOrder(OrderDto.Request orderRequest) {
        logger.info("call cancelOrder == {}", orderRequest);
        orderService.cancelOrder(orderRequest);
        return ResponseEntity.ok(orderRequest.getNo());
    }
}
