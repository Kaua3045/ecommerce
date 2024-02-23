package com.kaua.ecommerce.application.usecases.product.create;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal price,
        String categoryId,
        List<CreateProductCommandAttributes> attributes
) {

    public static CreateProductCommand with(
            final String aName,
            final String aDescription,
            final BigDecimal aPrice,
            final String aCategoryId,
            final List<CreateProductCommandAttributes> aAttributes
    ) {
        return new CreateProductCommand(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                aAttributes
        );
    }
}
