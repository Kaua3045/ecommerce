package com.kaua.ecommerce.application.usecases.product.attributes.remove;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.ProductIsDeletedException;
import com.kaua.ecommerce.application.exceptions.ProductNotHaveMoreAttributesException;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;

import java.util.Objects;

public class DefaultRemoveProductAttributesUseCase extends RemoveProductAttributesUseCase {

    private final ProductGateway productGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;

    public DefaultRemoveProductAttributesUseCase(
            final ProductGateway productGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void execute(RemoveProductAttributesCommand input) {
        final var aProduct = this.productGateway.findById(input.productId())
                .orElseThrow(NotFoundException.with(Product.class, input.productId()));

        if (aProduct.getStatus().equals(ProductStatus.DELETED)) {
            throw ProductIsDeletedException.with(aProduct.getId());
        }

        if (aProduct.getAttributes().isEmpty()) {
            throw new ProductNotHaveMoreAttributesException();
        }

        final var aResultAfterCallRemove = aProduct.removeAttribute(input.sku());

        if (aResultAfterCallRemove != null) {
            final var aTransactionResult = this.transactionManager.execute(() -> {
                final var aUpdateResult = this.productGateway.update(aResultAfterCallRemove);
                this.eventPublisher.publish(ProductUpdatedEvent.from(aResultAfterCallRemove));
                return aUpdateResult;
            });

            if (aTransactionResult.isFailure()) {
                throw TransactionFailureException.with(aTransactionResult.getErrorResult());
            }
        }
    }
}
