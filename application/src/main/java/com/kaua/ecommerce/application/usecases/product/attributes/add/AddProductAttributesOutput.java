package com.kaua.ecommerce.application.usecases.product.attributes.add;

import com.kaua.ecommerce.domain.product.Product;

public record AddProductAttributesOutput(String productId) {

    public static AddProductAttributesOutput from(final Product aProduct) {
        return new AddProductAttributesOutput(aProduct.getId().getValue());
    }
}
