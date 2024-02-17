package com.kaua.ecommerce.application.usecases.product.update;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.ProductIsDeletedException;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateProductUseCase extends UpdateProductUseCase {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;

    public DefaultUpdateProductUseCase(
            final ProductGateway productGateway,
            final CategoryGateway categoryGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Either<NotificationHandler, UpdateProductOutput> execute(UpdateProductCommand input) {
        final var aNotification = NotificationHandler.create();
        final var aProduct = this.productGateway.findById(input.id())
                .orElseThrow(NotFoundException.with(Product.class, input.id()));

        if (aProduct.getStatus().equals(ProductStatus.DELETED)) {
            throw ProductIsDeletedException.with(aProduct.getId());
        }

        final var aCategoryId = input.categoryId() == null || input.categoryId().isBlank()
                ? aProduct.getCategoryId()
                : this.categoryGateway.findById(input.categoryId())
                .orElseThrow(NotFoundException.with(Category.class, input.categoryId()))
                .getId();

        final var aName = input.name() == null || input.name().isBlank()
                ? aProduct.getName()
                : input.name();
        final var aPrice = input.price() == null
                ? aProduct.getPrice()
                : input.price();

        final var aProductUpdated = aProduct.update(
                aName,
                input.description(),
                aPrice,
                input.quantity(),
                aCategoryId
        );
        aProductUpdated.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        final var aResult = this.transactionManager.execute(() -> {
            final var aProductSaved = this.productGateway.update(aProductUpdated);
            this.eventPublisher.publish(ProductUpdatedEvent.from(aProductSaved));
            return aProductSaved;
        });

        if (aResult.isFailure()) {
            throw TransactionFailureException.with(aResult.getErrorResult());
        }

        return Either.right(UpdateProductOutput.from(aResult.getSuccessResult()));
    }
}
