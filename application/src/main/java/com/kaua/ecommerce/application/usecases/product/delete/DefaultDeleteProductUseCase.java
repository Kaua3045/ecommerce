package com.kaua.ecommerce.application.usecases.product.delete;

import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.product.events.ProductDeletedEvent;

import java.util.Objects;

public class DefaultDeleteProductUseCase extends DeleteProductUseCase {

    private final ProductGateway productGateway;

    public DefaultDeleteProductUseCase(final ProductGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public void execute(String aId) {
        this.productGateway.findById(aId).ifPresent(aProduct -> {
            aProduct.updateStatus(ProductStatus.DELETED);
            aProduct.registerEvent(ProductDeletedEvent.from(aProduct));
            this.productGateway.update(aProduct);
        });
    }
}
