package com.kaua.ecommerce.application.usecases.product.update;

import java.math.BigDecimal;

public record UpdateProductCommand(
        String id,
        String name,
        String description,
        BigDecimal price,
        String categoryId
) {

    public static UpdateProductCommand with(
            final String aId,
            final String aName,
            final String aDescription,
            final BigDecimal aPrice,
            final String aCategoryId
    ) {
        return new UpdateProductCommand(
                aId,
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );
    }
}
