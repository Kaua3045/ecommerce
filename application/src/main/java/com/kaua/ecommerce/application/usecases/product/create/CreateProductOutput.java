package com.kaua.ecommerce.application.usecases.product.create;

import com.kaua.ecommerce.domain.product.Product;

public record CreateProductOutput(String id) {

    public static CreateProductOutput from(final Product aProduct) {
        return new CreateProductOutput(aProduct.getId().getValue());
    }
}
