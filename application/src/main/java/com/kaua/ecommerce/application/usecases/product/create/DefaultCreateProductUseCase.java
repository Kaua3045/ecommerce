package com.kaua.ecommerce.application.usecases.product.create;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductAttributes;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.domain.product.ProductSize;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultCreateProductUseCase extends CreateProductUseCase {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;

    public DefaultCreateProductUseCase(
            final ProductGateway productGateway,
            final CategoryGateway categoryGateway
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, CreateProductOutput> execute(final CreateProductCommand aCommand) {
        final var aNotification = NotificationHandler.create();

        final var aCategory = this.categoryGateway.findById(aCommand.categoryId())
                .orElseThrow(NotFoundException.with(Category.class, aCommand.categoryId()));

        final var aAttributes = aCommand.attributes()
                .stream().map(attribute -> {
                    final var aColorName = attribute.colorName()
                            .toUpperCase();

                    final var aColor = this.productGateway.findColorByName(aColorName)
                            .orElseGet(() -> ProductColor.with(aColorName));

                    final var aProductSize = ProductSize.with(
                            attribute.sizeName(),
                            attribute.weight(),
                            attribute.height(),
                            attribute.width(),
                            attribute.depth());
                    return ProductAttributes.create(aColor, aProductSize, aCommand.name());
                }).collect(Collectors.toSet());

        final var aProduct = Product.newProduct(
                aCommand.name(),
                aCommand.description(),
                aCommand.price(),
                aCommand.quantity(),
                aCategory.getId(),
                aAttributes
        );
        aProduct.validate(aNotification);
        aProduct.registerEvent(ProductCreatedEvent.from(aProduct));

        return aNotification.hasError()
                ? Either.left(aNotification)
                : Either.right(CreateProductOutput.from(this.productGateway.create(aProduct)));
    }
}
