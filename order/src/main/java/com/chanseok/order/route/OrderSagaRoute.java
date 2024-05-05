package com.chanseok.order.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.springframework.stereotype.Component;

@Component
public class OrderSagaRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:order")
                .saga()
                    .compensation("direct:cancelOrder")
                    .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                    .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                    .setHeader("id", constant("SAGA Simple Test"))
                    .setHeader("Accept", constant("application/json"))
                    .to("http://localhost:8001/payments")
                .end()
        ;

        from("direct:cancelOrder")
                .saga()
                .log("======= Cancel Order ======")
                .end()
        ;
    }
}