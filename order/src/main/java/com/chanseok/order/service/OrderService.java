package com.chanseok.order.service;

import com.chanseok.order.dto.OrderDto;
import com.chanseok.order.entity.Order;
import com.chanseok.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;
    private final FluentProducerTemplate fluentProducerTemplate;

    @Transactional
    public OrderDto.Response createOrder(OrderDto.Request orderRequest) {
        // create order
        Order order = orderRepository.save(orderRequest.toEntity());
        try {
            String orderValue = objectMapper.writeValueAsString(order);
            producerTemplate.requestBody("direct:order", orderValue);
           fluentProducerTemplate.withBody(orderValue).withHeader("Content-Type", "application/json").to("direct:order").request();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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