package com.kaua.ecommerce.application.gateways.order;

import java.math.BigDecimal;
import java.util.Optional;

public interface OrderProductGateway {

    Optional<OrderProductDetails> getProductDetailsBySku(String sku);

    record OrderProductDetails(
            String sku,
            BigDecimal price,
            double weight,
            double width,
            double height,
            double length
    ) { }
}
