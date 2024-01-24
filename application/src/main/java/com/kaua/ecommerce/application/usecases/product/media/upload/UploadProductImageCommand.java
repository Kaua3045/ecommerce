package com.kaua.ecommerce.application.usecases.product.media.upload;

import com.kaua.ecommerce.domain.product.ProductImageResource;

import java.util.List;

public record UploadProductImageCommand(
        String productId,
        List<ProductImageResource> productImagesResources
) {

    public static UploadProductImageCommand with(
            final String aProductId,
            final List<ProductImageResource> aProductImagesResources
    ) {
        return new UploadProductImageCommand(aProductId, aProductImagesResources);
    }
}
