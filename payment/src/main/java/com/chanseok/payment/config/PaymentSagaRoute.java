package com.chanseok.payment.config;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.springframework.stereotype.Component;

@Component
public class PaymentSagaRoute extends RouteBuilder {

    @Override
    public void configure() {
        rest()
                .post("/payments")
                .to("direct:payment")
        ;

        from("direct:payment")
                .saga()
                    .propagation(SagaPropagation.MANDATORY)
                    .option("id", header("id"))
                    .compensation("direct:cancelPayment")
                    .log("Payment ${header.id} is processing...")
                    .choice().when(x -> x.getIn().getBody(String.class).contains("SAGA"))
                    .throwException(new RuntimeException("Payment Error"))
                .end()
        ;

        from("direct:cancelPayment")
                .log("======= Cancel Payment ======")
        ;
    }

}
