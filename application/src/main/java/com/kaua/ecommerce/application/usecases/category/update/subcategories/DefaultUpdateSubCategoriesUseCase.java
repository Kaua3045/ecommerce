package com.kaua.ecommerce.application.usecases.category.update.subcategories;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.SlugUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateSubCategoriesUseCase extends UpdateSubCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateSubCategoriesUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateSubCategoriesOutput> execute(UpdateSubCategoriesCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.categoryGateway.existsByName(input.name())) {
            return Either.left(aNotification.append(new Error("Category already exists")));
        }

        final var aCategoryRoot = this.categoryGateway.findById(input.id())
                .orElseThrow(NotFoundException.with(Category.class, input.id()));

        final var aSlug = nameToSlug(input.name());

        final var aSubCategory = Category.newCategory(
                input.name(),
                input.description(),
                aSlug,
                aCategoryRoot.getId()
        );
        aSubCategory.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();

        return Either.right(UpdateSubCategoriesOutput.from(this.categoryGateway.update(aCategoryRoot)));
    }

    private String nameToSlug(final String aName) {
        return SlugUtils.createSlug(aName);
    }
}
