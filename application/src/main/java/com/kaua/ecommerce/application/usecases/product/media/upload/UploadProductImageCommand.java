package com.kaua.ecommerce.application.usecases.product.media.upload;

import com.kaua.ecommerce.domain.product.ProductImageResource;

public record UploadProductImageCommand(
        String productId,
        ProductImageResource productImageResource
) {

    public static UploadProductImageCommand with(
            final String aProductId,
            final ProductImageResource aProductImageResource
    ) {
        return new UploadProductImageCommand(aProductId, aProductImageResource);
    }
}
