package com.kaua.ecommerce.application.usecases.product.media.upload;

import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductImageType;

public record UploadProductImageOutput(String productId, ProductImageType productImageType) {

    public static UploadProductImageOutput from(
            final Product aProduct,
            final ProductImageType aType
    ) {
        return new UploadProductImageOutput(aProduct.getId().getValue(), aType);
    }
}
