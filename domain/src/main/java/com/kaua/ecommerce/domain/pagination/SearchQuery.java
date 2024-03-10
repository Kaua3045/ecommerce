package com.kaua.ecommerce.domain.pagination;

import java.util.Optional;

public record SearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction,
        Period period
) {

    public SearchQuery(int page, int perPage, String terms, String sort, String direction) {
        this(page, perPage, terms, sort, direction, null);
    }
}
