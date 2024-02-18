package com.kaua.ecommerce.application.usecases.product.retrieve.get;

import com.kaua.ecommerce.domain.product.ProductAttributes;

public record GetProductByIdOutputAttributes(
        String colorId,
        String colorName,
        String sizeId,
        String sizeName,
        double weight,
        double height,
        double width,
        double depth,
        String sku
) {

    public static GetProductByIdOutputAttributes from(final ProductAttributes aAttributes) {
        return new GetProductByIdOutputAttributes(
                aAttributes.getColor().getId(),
                aAttributes.getColor().getColor(),
                aAttributes.getSize().getId(),
                aAttributes.getSize().getSize(),
                aAttributes.getSize().getWeight(),
                aAttributes.getSize().getHeight(),
                aAttributes.getSize().getWidth(),
                aAttributes.getSize().getDepth(),
                aAttributes.getSku()
        );
    }
}
