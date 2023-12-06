package com.kaua.ecommerce.application.usecases.category.search.save;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultSaveCategoryUseCase extends UseCase<Category, Category> {

    private final SearchGateway<Category> categorySearchGateway;

    public DefaultSaveCategoryUseCase(final SearchGateway<Category> categorySearchGateway) {
        this.categorySearchGateway = Objects.requireNonNull(categorySearchGateway);
    }

    @Override
    public Category execute(final Category aCategory) {
        if (aCategory == null) {
            throw DomainException.with(new Error("'aCategory' cannot be null"));
        }

        final var aNotification = NotificationHandler.create();
        aCategory.validate(aNotification);

        if (aNotification.hasError()) {
            throw DomainException.with(aNotification.getErrors());
        }

        return this.categorySearchGateway.save(aCategory);
    }
}
