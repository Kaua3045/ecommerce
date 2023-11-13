package com.kaua.ecommerce.application.usecases.category.create;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.utils.SlugUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, CreateCategoryOutput> execute(CreateCategoryCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.categoryGateway.existsByName(input.name())) {
            return Either.left(aNotification.append(new Error("Category already exists")));
        }

        final var aSlug = nameToSlug(input.name());

        final var aCategory = Category.newCategory(
                input.name(),
                input.description(),
                aSlug,
                input.isRoot()
        );
        aCategory.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        return Either.right(CreateCategoryOutput.from(this.categoryGateway.create(aCategory)));
    }

    private String nameToSlug(final String aName) {
        return SlugUtils.createSlug(aName);
    }
}
