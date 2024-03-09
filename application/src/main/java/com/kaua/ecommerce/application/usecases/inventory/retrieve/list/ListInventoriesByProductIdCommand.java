package com.kaua.ecommerce.application.usecases.inventory.retrieve.list;

import com.kaua.ecommerce.domain.pagination.SearchQuery;

public record ListInventoriesByProductIdCommand(
        String productId,
        SearchQuery searchQuery
) {

    public static ListInventoriesByProductIdCommand with(final String productId, final SearchQuery searchQuery) {
        return new ListInventoriesByProductIdCommand(productId, searchQuery);
    }
}
