package com.kaua.ecommerce.domain.pagination;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PaginationTest {

    @Test
    void givenAValidValues_whenCallNewPagination_thenShouldReturnAPagination() {
        final var page = 0;
        final var perPage = 10;
        final var totalPages = 1;
        final var totalItems = 1;
        final var items = List.of(new PaginationDummy("name"));

        final var pagination = new Pagination<>(page, perPage, totalPages, totalItems, items);
        final var aResultWithMap = pagination.map(PaginationDummy::getName);

        Assertions.assertEquals(page, pagination.currentPage());
        Assertions.assertEquals(perPage, pagination.perPage());
        Assertions.assertEquals(totalPages, pagination.totalPages());
        Assertions.assertEquals(totalItems, pagination.totalItems());
        Assertions.assertEquals(items, pagination.items());
        Assertions.assertNotNull(aResultWithMap);
    }

    record PaginationDummy(String name) {
        public String getName() {
            return name;
        }
    }
}
