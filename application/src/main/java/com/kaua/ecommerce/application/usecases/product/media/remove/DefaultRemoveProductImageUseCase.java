package com.kaua.ecommerce.application.usecases.product.media.remove;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.ProductIsDeletedException;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;

import java.util.Objects;

public class DefaultRemoveProductImageUseCase extends RemoveProductImageUseCase {

    private final ProductGateway productGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;

    public DefaultRemoveProductImageUseCase(
            final ProductGateway productGateway,
            final MediaResourceGateway mediaResourceGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void execute(RemoveProductImageCommand input) {
        final var aProduct = this.productGateway.findById(input.productId())
                .orElseThrow(NotFoundException.with(Product.class, input.productId()));

        if (aProduct.getStatus().equals(ProductStatus.DELETED)) {
            throw ProductIsDeletedException.with(aProduct.getId());
        }

        aProduct.getBannerImage().ifPresent(bannerImage -> {
            if (bannerImage.getLocation().equalsIgnoreCase(input.location())) {
                this.mediaResourceGateway.clearImage(bannerImage);
                aProduct.changeBannerImage(null);

                final var aTransactionResult = this.transactionManager.execute(() -> {
                    final var aResult = this.productGateway.update(aProduct);
                    this.eventPublisher.publish(ProductUpdatedEvent.from(aProduct));
                    return aResult;
                });

                if (aTransactionResult.isFailure()) {
                    throw TransactionFailureException.with(aTransactionResult.getErrorResult());
                }
            }
        });

        final var aProductImageToRemove = aProduct.removeImage(input.location());

        if (aProductImageToRemove != null) {
            this.mediaResourceGateway.clearImage(aProductImageToRemove);

            final var aTransactionResult = this.transactionManager.execute(() -> {
                final var aResult = this.productGateway.update(aProduct);
                this.eventPublisher.publish(ProductUpdatedEvent.from(aProduct));
                return aResult;
            });

            if (aTransactionResult.isFailure()) {
                throw TransactionFailureException.with(aTransactionResult.getErrorResult());
            }
        }
    }
}
