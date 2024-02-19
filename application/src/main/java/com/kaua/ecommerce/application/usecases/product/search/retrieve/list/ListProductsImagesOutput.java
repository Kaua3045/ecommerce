package com.kaua.ecommerce.application.usecases.product.search.retrieve.list;

import com.kaua.ecommerce.domain.product.ProductImage;

public record ListProductsImagesOutput(
        String location,
        String url
) {

    public static ListProductsImagesOutput from(final ProductImage aProductImage) {
        return new ListProductsImagesOutput(aProductImage.getLocation(), aProductImage.getUrl());
    }
}
