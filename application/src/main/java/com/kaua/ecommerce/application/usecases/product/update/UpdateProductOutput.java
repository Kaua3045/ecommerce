package com.kaua.ecommerce.application.usecases.product.update;

import com.kaua.ecommerce.domain.product.Product;

public record UpdateProductOutput(String id) {

    public static UpdateProductOutput from(final Product aProduct) {
        return new UpdateProductOutput(aProduct.getId().getValue());
    }
}
