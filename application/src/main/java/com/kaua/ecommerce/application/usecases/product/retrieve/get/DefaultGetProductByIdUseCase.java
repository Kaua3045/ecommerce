package com.kaua.ecommerce.application.usecases.product.retrieve.get;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;

import java.util.Objects;

public class DefaultGetProductByIdUseCase extends GetProductByIdUseCase {

    private final ProductGateway productGateway;

    public DefaultGetProductByIdUseCase(final ProductGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public GetProductByIdOutput execute(String aId) {
        return this.productGateway.findById(aId)
                .map(GetProductByIdOutput::from)
                .orElseThrow(NotFoundException.with(Product.class, aId));
    }
}
