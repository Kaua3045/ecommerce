package com.kaua.ecommerce.application.usecases.category.update;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.SlugUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateCategoryOutput> execute(final UpdateCategoryCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.categoryGateway.existsByName(input.name())) {
            return Either.left(aNotification.append(new Error("Category already exists")));
        }

        final var aCategory = this.categoryGateway.findById(input.id())
                .orElseThrow(NotFoundException.with(Category.class, input.id()));

        final var aName = validateName(input, aCategory);
        final var aSlug = nameToSlug(aName);
        final var aCategoryUpdated = aCategory.update(aName, input.description(), aSlug);
        aCategoryUpdated.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        this.categoryGateway.update(aCategoryUpdated);

        return Either.right(UpdateCategoryOutput.from(aCategoryUpdated));
    }

    private String validateName(final UpdateCategoryCommand input, final Category aOldCategoryName) {
        return input.name() == null || input.name().isBlank()
                ? aOldCategoryName.getName()
                : input.name();
    }

    private String nameToSlug(final String aName) {
        return SlugUtils.createSlug(aName);
    }
}
