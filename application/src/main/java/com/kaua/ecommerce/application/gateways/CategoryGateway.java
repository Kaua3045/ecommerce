package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.category.Category;

import java.util.Optional;

public interface CategoryGateway {

    Category create(Category aCategory);

    Optional<Category> findByName(String aName);
}
