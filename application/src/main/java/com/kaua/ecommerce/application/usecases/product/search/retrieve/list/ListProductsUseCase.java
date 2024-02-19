package com.kaua.ecommerce.application.usecases.product.search.retrieve.list;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.product.Product;

import java.util.Objects;

public class ListProductsUseCase extends UseCase<Pagination<ListProductsOutput>, SearchQuery> {

    private final SearchGateway<Product> searchProductGateway;

    public ListProductsUseCase(final SearchGateway<Product> searchProductGateway) {
        this.searchProductGateway = Objects.requireNonNull(searchProductGateway);
    }

    @Override
    public Pagination<ListProductsOutput> execute(SearchQuery aQuery) {
        return this.searchProductGateway.findAll(aQuery)
                .map(ListProductsOutput::from);
    }
}
