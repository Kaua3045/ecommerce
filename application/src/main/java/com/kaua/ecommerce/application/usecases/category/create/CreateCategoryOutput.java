package com.kaua.ecommerce.application.usecases.category.create;

import com.kaua.ecommerce.domain.category.Category;

public record CreateCategoryOutput(String id) {

    public static CreateCategoryOutput from(final Category aCategory) {
        return new CreateCategoryOutput(aCategory.getId().getValue());
    }
}
