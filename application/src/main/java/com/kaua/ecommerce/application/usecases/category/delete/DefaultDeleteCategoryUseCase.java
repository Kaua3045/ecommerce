package com.kaua.ecommerce.application.usecases.category.delete;

import com.kaua.ecommerce.application.gateways.CategoryGateway;

import java.util.Objects;

public class DefaultDeleteCategoryUseCase extends DeleteCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultDeleteCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public void execute(String aId) {
        this.categoryGateway.deleteById(aId);
    }
}
