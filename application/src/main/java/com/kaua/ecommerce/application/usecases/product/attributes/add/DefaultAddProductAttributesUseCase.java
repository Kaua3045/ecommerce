package com.kaua.ecommerce.application.usecases.product.attributes.add;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.ProductIsDeletedException;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.*;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.domain.validation.Error;

import java.util.Objects;

public class DefaultAddProductAttributesUseCase extends AddProductAttributesUseCase {

    private final ProductGateway productGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;

    public DefaultAddProductAttributesUseCase(
            final ProductGateway productGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
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

        aAttributesParams.forEach(aParam -> {
            final var aProductAttribute = ProductAttributes.create(
                    ProductColor.with(aParam.colorName()),
                    ProductSize.with(
                            aParam.sizeName(),
                            aParam.weight(),
                            aParam.height(),
                            aParam.width(),
                            aParam.depth()
                    ),
                    aProduct.getName()
            );
            aProduct.addAttribute(aProductAttribute);
        });

        final var aResult = this.transactionManager.execute(() -> {
            final var aProductSaved = this.productGateway.update(aProduct);
            this.eventPublisher.publish(ProductUpdatedEvent.from(aProductSaved));
            return aProductSaved;
        });

        if (aResult.isFailure()) {
            throw TransactionFailureException.with(aResult.getErrorResult());
        }

        return AddProductAttributesOutput.from(aProduct);
    }
}
