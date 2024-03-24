package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderProductGateway;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderProductGatewayImpl implements OrderProductGateway {

    // TODO: Implement the method getProductDetailsBySku

    @Override
    public Optional<OrderProductDetails> getProductDetailsBySku(String sku) {
        return Optional.empty();
    }
}
