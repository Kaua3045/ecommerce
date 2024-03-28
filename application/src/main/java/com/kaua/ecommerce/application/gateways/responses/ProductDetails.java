package com.kaua.ecommerce.application.gateways.responses;

import java.math.BigDecimal;

public record ProductDetails(
        String sku,
        BigDecimal price,
        double weight,
        double width,
        double height,
        double length
) {
}
