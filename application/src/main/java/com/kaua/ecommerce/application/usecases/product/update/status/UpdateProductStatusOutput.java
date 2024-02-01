package com.kaua.ecommerce.application.usecases.product.update.status;

import com.kaua.ecommerce.domain.product.Product;

public record UpdateProductStatusOutput(String id) {

    public static UpdateProductStatusOutput from(final Product aProduct) {
        return new UpdateProductStatusOutput(aProduct.getId().getValue());
    }
}
