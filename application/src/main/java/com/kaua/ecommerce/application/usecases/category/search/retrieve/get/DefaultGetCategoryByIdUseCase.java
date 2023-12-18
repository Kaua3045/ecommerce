package com.kaua.ecommerce.application.usecases.category.search.retrieve.get;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultGetCategoryByIdUseCase extends UseCase<GetCategoryByIdOutput, String> {

    private final SearchGateway<Category> categorySearchGateway;

    public DefaultGetCategoryByIdUseCase(final SearchGateway<Category> categorySearchGateway) {
        this.categorySearchGateway = Objects.requireNonNull(categorySearchGateway);
    }

    @Override
    public GetCategoryByIdOutput execute(final String aId) {
        return this.categorySearchGateway.findByIdNested(aId)
                .map(GetCategoryByIdOutput::from)
                .orElseThrow(NotFoundException.with(Category.class, aId));
    }
}
