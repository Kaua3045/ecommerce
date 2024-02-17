package com.kaua.ecommerce.application.usecases.product.update.status;

import com.kaua.ecommerce.application.exceptions.ProductIsDeletedException;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.domain.validation.Error;

import java.util.Objects;

public class DefaultUpdateProductStatusUseCase extends UpdateProductStatusUseCase {

    private final ProductGateway productGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;

    public DefaultUpdateProductStatusUseCase(
            final ProductGateway productGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public UpdateProductStatusOutput execute(UpdateProductStatusCommand input) {
        final var aProduct = this.productGateway.findById(input.id())
                .orElseThrow(NotFoundException.with(Product.class, input.id()));

        final var aStatus = ProductStatus.of(input.status())
                .orElseThrow(() -> DomainException
                        .with(new Error("status %s was not found".formatted(input.status()))));

        if (aStatus.equals(ProductStatus.DELETED)) {
            throw DomainException.with(new Error("status DELETED is not allowed to be set"));
        }

        if (aProduct.getStatus().equals(ProductStatus.DELETED)) {
            throw ProductIsDeletedException.with(aProduct.getId());
        }

        final var aProductUpdated = aProduct.updateStatus(aStatus);

        final var aResult = this.transactionManager.execute(() -> {
            final var aProductSaved = this.productGateway.update(aProductUpdated);
            this.eventPublisher.publish(ProductUpdatedEvent.from(aProductSaved));
            return aProductSaved;
        });

        if (aResult.isFailure()) {
            throw TransactionFailureException.with(aResult.getErrorResult());
        }

        return UpdateProductStatusOutput.from(aResult.getSuccessResult());
    }
}
