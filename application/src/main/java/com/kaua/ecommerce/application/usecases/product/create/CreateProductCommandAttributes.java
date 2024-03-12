package com.kaua.ecommerce.application.usecases.product.create;

public record CreateProductCommandAttributes(
        String colorName,
        String sizeName,
        double weight,
        double height,
        double width,
        double length,
        int quantity
) {

    public static CreateProductCommandAttributes with(
            final String aColorName,
            final String aSizeName,
            final double aWeight,
            final double aHeight,
            final double aWidth,
            final double aLength,
            final int aQuantity
    ) {
        return new CreateProductCommandAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );
    }
}
