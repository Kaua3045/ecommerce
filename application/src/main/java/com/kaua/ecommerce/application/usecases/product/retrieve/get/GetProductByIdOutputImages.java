package com.kaua.ecommerce.application.usecases.product.retrieve.get;

import com.kaua.ecommerce.domain.product.ProductImage;

public record GetProductByIdOutputImages(
        String location,
        String url
) {

    public static GetProductByIdOutputImages from(final ProductImage aProductImage) {
        return new GetProductByIdOutputImages(aProductImage.getLocation(), aProductImage.getUrl());
    }
}
