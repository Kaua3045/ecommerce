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
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

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

        final var aColor = this.productGateway.findColorByName(aCommand.colorName());

        final var aProductColor = aColor.orElseGet(() -> ProductColor.with(aCommand.colorName()));
        final var aProductSize = ProductSize.with(
                aCommand.sizeName(),
                aCommand.weight(),
                aCommand.height(),
                aCommand.width(),
                aCommand.depth());

        final var aAttributes = ProductAttributes.create(aProductColor, aProductSize, aCommand.name());

        final var aProduct = Product.newProduct(
                aCommand.name(),
                aCommand.description(),
                aCommand.price(),
                aCommand.quantity(),
                aCategory.getId(),
                aAttributes
        );
        aProduct.validate(aNotification);

//        return Either.right(Output.from(this.productGateway.findById("f340ab1c-d692-4c40-bc63-1b823c7f68bb").get()));

        return aNotification.hasError()
                ? Either.left(aNotification)
                : Either.right(CreateProductOutput.from(this.productGateway.create(aProduct)));
    }
}
