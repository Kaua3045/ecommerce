package com.kaua.ecommerce.domain.pagination;

import com.kaua.ecommerce.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearchQueryTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewSearchQuery_shouldReturnASearchQueryInstance() {
        final var page = 0;
        final var perPage = 10;
        final var terms = "";
        final var sort = "createdAt";
        final var direction = "asc";

        final var aQuery = new SearchQuery(page, perPage, terms, sort, direction);

        Assertions.assertEquals(page, aQuery.page());
        Assertions.assertEquals(perPage, aQuery.perPage());
        Assertions.assertEquals(terms, aQuery.terms());
        Assertions.assertEquals(sort, aQuery.sort());
        Assertions.assertEquals(direction, aQuery.direction());
        Assertions.assertNull(aQuery.period());
    }

    @Test
    void givenAValidValues_whenCallNewSearchQueryWithPeriod_shouldReturnASearchQueryInstance() {
        final var page = 0;
        final var perPage = 10;
        final var terms = "";
        final var sort = "createdAt";
        final var direction = "asc";
        final var period = new Period("2021-01-01T00:00:00Z", "2021-01-31T23:59:59Z");

        final var aQuery = new SearchQuery(page, perPage, terms, sort, direction, period);

        Assertions.assertEquals(page, aQuery.page());
        Assertions.assertEquals(perPage, aQuery.perPage());
        Assertions.assertEquals(terms, aQuery.terms());
        Assertions.assertEquals(sort, aQuery.sort());
        Assertions.assertEquals(direction, aQuery.direction());
        Assertions.assertEquals(period, aQuery.period());
    }
}
