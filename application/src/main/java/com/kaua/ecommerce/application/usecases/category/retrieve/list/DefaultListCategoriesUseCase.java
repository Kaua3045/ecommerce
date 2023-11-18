package com.kaua.ecommerce.application.usecases.category.retrieve.list;

import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;

import java.util.Objects;

public class DefaultListCategoriesUseCase extends ListCategoriesUseCase {

    private final SearchGateway<Category> searchCategoryGateway;

    public DefaultListCategoriesUseCase(final SearchGateway<Category> searchCategoryGateway) {
        this.searchCategoryGateway = Objects.requireNonNull(searchCategoryGateway);
    }

    @Override
    public Pagination<ListCategoriesOutput> execute(final SearchQuery aQuery) {
        return this.searchCategoryGateway.findAll(aQuery)
                .map(ListCategoriesOutput::from);
    }
}
