package com.chanseok.orchestrator.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.springframework.stereotype.Component;

@Component
public class OrderSagaRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("netty-http:localhost:8010/payment")
                .saga()
                .compensation("direct:cancelOrder")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .log("======= OrderSagaRoute ======")
                .to("netty-http:localhost:8001/payment")
                .end()
        ;

        from("direct:cancelOrder")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .log("======= Cancel Order ======")
                .to("netty-http:localhost:8080/order/cancel")
                .end()
        ;
    }
}