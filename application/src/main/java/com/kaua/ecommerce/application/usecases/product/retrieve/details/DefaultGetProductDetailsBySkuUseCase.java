package com.kaua.ecommerce.application.usecases.product.retrieve.details;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.responses.ProductDetails;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;

import java.util.Objects;

public class DefaultGetProductDetailsBySkuUseCase extends GetProductDetailsBySkuUseCase {

    private final ProductGateway productGateway;

    public DefaultGetProductDetailsBySkuUseCase(final ProductGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public ProductDetails execute(String aSku) {
        return this.productGateway.findProductDetailsBySku(aSku)
                .orElseThrow(NotFoundException.with(Product.class, aSku));
    }
}
