package com.chanseok.order.service;

import com.chanseok.order.dto.OrderDto;
import com.chanseok.order.entity.Order;
import com.chanseok.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, ProducerTemplate producerTemplate, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.producerTemplate = producerTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderDto.Response createOrder(OrderDto.Request orderRequest) {
        // create order
        Order order = orderRepository.save(orderRequest.toEntity());
        try {
            String orderValue = objectMapper.writeValueAsString(order);
            producerTemplate.sendBody("netty-http:localhost:8010/payment", orderValue);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new OrderDto.Response(order.getNo(), order.getName(), order.getOrderState(), order.getPrice(), order.getQuantity());
    }

    @Transactional
    public void cancelOrder(OrderDto.Request orderRequest) {
        logger.info("call cancelOrder == {}", orderRequest);
        Order order = orderRequest.toEntity();
        orderRepository.delete(order);
    }
}