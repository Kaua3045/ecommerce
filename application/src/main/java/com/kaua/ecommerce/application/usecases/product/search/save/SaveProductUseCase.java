package com.kaua.ecommerce.application.usecases.product.search.save;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class SaveProductUseCase extends UseCase<Product, Product> {

    private final SearchGateway<Product> productSearchGateway;

    public SaveProductUseCase(final SearchGateway<Product> productSearchGateway) {
        this.productSearchGateway = Objects.requireNonNull(productSearchGateway);
    }

    @Override
    public Product execute(Product aProduct) {
        if (aProduct == null) {
            throw DomainException.with(new Error("Product cannot be null"));
        }

        final var aNotification = NotificationHandler.create();
        aProduct.validate(aNotification);

        if (aNotification.hasError()) {
            throw DomainException.with(aNotification.getErrors());
        }

        return this.productSearchGateway.save(aProduct);
    }
}
