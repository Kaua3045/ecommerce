package com.kaua.ecommerce.application.usecases.product.create;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductAttributes;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.domain.product.ProductSize;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.utils.CollectionUtils;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultCreateProductUseCase extends CreateProductUseCase {

    private final ProductGateway productGateway;
    private final CategoryGateway categoryGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;
    private final ProductInventoryGateway productInventoryGateway;

    public DefaultCreateProductUseCase(
            final ProductGateway productGateway,
            final CategoryGateway categoryGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher,
            final ProductInventoryGateway productInventoryGateway
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.productInventoryGateway = Objects.requireNonNull(productInventoryGateway);
    }

    @Override
    public Either<NotificationHandler, CreateProductOutput> execute(final CreateProductCommand aCommand) {
        final var aNotification = NotificationHandler.create();

        final var aCategory = this.categoryGateway.findById(aCommand.categoryId())
                .orElseThrow(NotFoundException.with(Category.class, aCommand.categoryId()));

        final var aAttributes = this.mapToProductAttributesWithQuantity(
                aCommand.name(), aCommand.attributes());

        final var aProduct = Product.newProduct(
                aCommand.name(),
                aCommand.description(),
                aCommand.price(),
                aCategory.getId(),
                CollectionUtils.mapTo(aAttributes, AbstractMap.SimpleImmutableEntry::getKey)
        );
        aProduct.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        final var aCreateInventoryResult = this.createInventoryToAttributes(aProduct, aAttributes);

        if (aCreateInventoryResult.isLeft()) {
            return Either.left(aCreateInventoryResult.getLeft());
        }

        final var aResult = this.createProduct(aProduct);

        if (aResult.isFailure()) {
            throw TransactionFailureException.with(aResult.getErrorResult());
        }

        return Either.right(CreateProductOutput.from(aResult.getSuccessResult()));
    }

    private Set<AbstractMap.SimpleImmutableEntry<ProductAttributes, Integer>> mapToProductAttributesWithQuantity(
            final String aProductName,
            final List<CreateProductCommandAttributes> aAttributes
    ) {
        return aAttributes.stream().map(attribute -> {
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
            final var aProductAttribute = ProductAttributes.create(aColor, aProductSize, aProductName);
            return new AbstractMap.SimpleImmutableEntry<>(aProductAttribute, attribute.quantity());
        }).collect(Collectors.toSet());
    }

    private Either<NotificationHandler, Void> createInventoryToAttributes(
            final Product aProduct,
            final Set<AbstractMap.SimpleImmutableEntry<ProductAttributes, Integer>> aAttributes
    ) {
        final var aCreateInventoryResult = this.productInventoryGateway.createInventory(
                aProduct.getId().getValue(),
                aAttributes.stream()
                        .map(it -> new ProductInventoryGateway.CreateInventoryParams(
                                it.getKey().getSku(),
                                it.getValue()
                        )).toList()
        );

        if (aCreateInventoryResult.isLeft()) {
            return Either.left(aCreateInventoryResult.getLeft());
        }

        return Either.right(null);
    }

    private TransactionResult<Product> createProduct(final Product aProduct) {
        return this.transactionManager.execute(() -> {
            final var aProductSaved = this.productGateway.create(aProduct);
            this.eventPublisher.publish(ProductCreatedEvent.from(aProductSaved));
            return aProductSaved;
        });
    }
}
