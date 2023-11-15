package com.kaua.ecommerce.application.usecases.category.create;

import com.kaua.ecommerce.domain.category.Category;

public record CreateCategoryRootOutput(String id) {

    public static CreateCategoryRootOutput from(final Category aCategory) {
        return new CreateCategoryRootOutput(aCategory.getId().getValue());
    }
}
