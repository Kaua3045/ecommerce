package com.kaua.ecommerce.application.usecases.product.media.upload;

import com.kaua.ecommerce.domain.product.Product;

public record UploadProductImageOutput(String productId) {

    public static UploadProductImageOutput from(final Product aProduct) {
        return new UploadProductImageOutput(aProduct.getId().getValue());
    }
}
