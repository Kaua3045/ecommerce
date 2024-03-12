package com.kaua.ecommerce.application.usecases.product.attributes.add;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.ProductIsDeletedException;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.inventory.events.InventoryCreatedRollbackBySkusEvent;
import com.kaua.ecommerce.domain.product.*;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.domain.validation.Error;

import java.util.ArrayList;
import java.util.Objects;

public class DefaultAddProductAttributesUseCase extends AddProductAttributesUseCase {

    private final ProductGateway productGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;
    private final ProductInventoryGateway productInventoryGateway;

    public DefaultAddProductAttributesUseCase(
            final ProductGateway productGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher,
            final ProductInventoryGateway productInventoryGateway
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.productInventoryGateway = Objects.requireNonNull(productInventoryGateway);
    }

    @Override
    public AddProductAttributesOutput execute(AddProductAttributesCommand input) {
        final var aProduct = this.productGateway.findById(input.productId())
                .orElseThrow(NotFoundException.with(Product.class, input.productId()));
        final var aProductId = aProduct.getId();
        final var aAttributesParams = input.attributesParams();

        if (aProduct.getStatus().equals(ProductStatus.DELETED)) {
            throw ProductIsDeletedException.with(aProductId);
        }

        if (aAttributesParams.isEmpty()) {
            throw DomainException.with(new Error("'attributes' must have at least one element"));
        }

        final var aAttributesToCreateInventories = new ArrayList<ProductInventoryGateway.CreateInventoryParams>();

        aAttributesParams.forEach(aParam -> {
            final var aColorName = aParam.colorName()
                    .toUpperCase();

            final var aColor = this.productGateway.findColorByName(aColorName)
                    .orElseGet(() -> ProductColor.with(aColorName));

            final var aProductAttribute = ProductAttributes.create(
                    aColor,
                    ProductSize.with(
                            aParam.sizeName(),
                            aParam.weight(),
                            aParam.height(),
                            aParam.width(),
                            aParam.length()
                    ),
                    aProduct.getName()
            );
            aProduct.addAttribute(aProductAttribute);

            aAttributesToCreateInventories.add(new ProductInventoryGateway.CreateInventoryParams(
                    aProductAttribute.getSku(),
                    aParam.quantity()
            ));
        });

        final var aCreateInventoriesResult = this.productInventoryGateway.createInventory(
                aProductId.getValue(),
                aAttributesToCreateInventories
        );

        if (aCreateInventoriesResult.isLeft()) {
            throw DomainException.with(aCreateInventoriesResult.getLeft().getErrors());
        }

        final var aResult = this.transactionManager.execute(() -> {
            final var aProductSaved = this.productGateway.update(aProduct);
            this.eventPublisher.publish(ProductUpdatedEvent.from(aProductSaved));
            return aProductSaved;
        });

        if (aResult.isFailure()) {
            this.eventPublisher.publish(InventoryCreatedRollbackBySkusEvent.from(
                    aAttributesToCreateInventories.stream()
                            .map(ProductInventoryGateway.CreateInventoryParams::sku)
                            .toList()
            ));
            throw TransactionFailureException.with(aResult.getErrorResult());
        }

        return AddProductAttributesOutput.from(aProduct);
    }
}
