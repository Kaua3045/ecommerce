package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;

import java.util.Optional;

public interface CategoryGateway {

    Category create(Category aCategory);

    boolean existsByName(String aName);

    Optional<Category> findById(String aId);

    Pagination<Category> findAll(SearchQuery aQuery);

    Category update(Category aCategory);

    void deleteById(String aId);
}
