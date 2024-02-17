package com.kaua.ecommerce.application.usecases.product.delete;

import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.product.events.ProductDeletedEvent;

import java.util.Objects;

public class DefaultDeleteProductUseCase extends DeleteProductUseCase {

    private final ProductGateway productGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;

    public DefaultDeleteProductUseCase(
            final ProductGateway productGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void execute(String aId) {
        this.transactionManager.execute(() -> {
            this.productGateway.findById(aId).ifPresent(aProduct -> {
                aProduct.updateStatus(ProductStatus.DELETED);

                this.productGateway.update(aProduct);
                this.eventPublisher.publish(ProductDeletedEvent.from(aProduct));
            });
            return null;
        });
    }
}
