package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.category.Category;

import java.util.Optional;

public interface CategoryGateway {

    Category create(Category aCategory);

    boolean existsByName(String aName);

    Optional<Category> findById(String aId);

    Category update(Category aCategory);

    void deleteById(String aId);

    void deleteRootCategoryById(String aId);
}
