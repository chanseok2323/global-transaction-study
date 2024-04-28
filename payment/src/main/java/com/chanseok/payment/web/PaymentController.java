package com.chanseok.payment.web;

import com.chanseok.payment.dto.PaymentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class PaymentController {
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/payment")
    public PaymentDto payment(@RequestBody Map<String, String> param, @RequestHeader Map<String, Object> headers) {
        logger.info("orderId: {}", param);
        logger.info("headers: {}", headers);

        if(true) throw new RuntimeException("Payment Error");

        PaymentDto paymentDto1 = new PaymentDto(UUID.randomUUID().toString(), 3, 1000);
        return paymentDto1;
    }

}
