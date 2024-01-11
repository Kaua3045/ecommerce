package com.kaua.ecommerce.application.usecases.product.create;

import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal price,
        int quantity,
        String categoryId,
        String colorName,
        String sizeName,
        double weight,
        double height,
        double width,
        double depth
) {

    public static CreateProductCommand with(
            final String aName,
            final String aDescription,
            final BigDecimal aPrice,
            final int aQuantity,
            final String aCategoryId,
            final String aColorName,
            final String aSizeName,
            final double aWeight,
            final double aHeight,
            final double aWidth,
            final double aDepth
    ) {
        return new CreateProductCommand(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );
    }
}
