package com.kaua.ecommerce.application.usecases.category.delete;

import com.kaua.ecommerce.application.gateways.CategoryGateway;

import java.util.Objects;

public class DefaultDeleteCategoryUseCase extends DeleteCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultDeleteCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public void execute(String aId) {
        final var aCategory = this.categoryGateway.findById(aId);

        aCategory.ifPresent(category -> category.getParentId().ifPresentOrElse(aParentId -> {
            final var aParentCategory = this.categoryGateway.findById(aParentId.getValue());

            aParentCategory.ifPresent(aParent -> {
                aParent.removeSubCategory(category);
                aParent.updateRemoveSubCategoriesLevel();
                this.categoryGateway.update(aParent);
                this.categoryGateway.deleteById(aId);
            });
        }, () -> this.categoryGateway.deleteById(aId)));
    }
}
