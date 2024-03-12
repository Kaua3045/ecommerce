package com.kaua.ecommerce.application.usecases.product.attributes.add;

public record AddProductAttributesCommandParams(
        String colorName,
        String sizeName,
        double weight,
        double height,
        double width,
        double length,
        int quantity
) {
}
