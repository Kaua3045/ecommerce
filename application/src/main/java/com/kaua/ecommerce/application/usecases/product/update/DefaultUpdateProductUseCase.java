package com.kaua.ecommerce.application.usecases.product.update;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateProductUseCase extends UpdateProductUseCase {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;

    public DefaultUpdateProductUseCase(final ProductGateway productGateway, final CategoryGateway categoryGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateProductOutput> execute(UpdateProductCommand input) {
        final var aNotification = NotificationHandler.create();
        final var aProduct = this.productGateway.findById(input.id())
                .orElseThrow(NotFoundException.with(Product.class, input.id()));

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

        return Either.right(UpdateProductOutput.from(this.productGateway.update(aProductUpdated)));
    }
}
