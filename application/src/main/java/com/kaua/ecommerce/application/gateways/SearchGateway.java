package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;

import java.util.Optional;

public interface SearchGateway<T extends AggregateRoot> {

    T save(T aggregateRoot);

    Pagination<T> findAll(SearchQuery aQuery);

    Optional<T> findById(String id);

    Optional<T> findByIdNested(String id);

    void deleteById(String id);
}
