package com.kaua.ecommerce.domain.category;

import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

import static com.kaua.ecommerce.domain.utils.CommonErrorMessage.lengthBetween;

public class CategoryValidation extends Validator {

    private final Category category;
    private static final int MINIMUM_LENGTH = 3;
    private static final int MAXIMUM_LENGTH = 255;
    private static final int MAXIMUM_DESCRIPTION_LENGTH = 1000;

    protected CategoryValidation(final Category category, final ValidationHandler handler) {
        super(handler);
        this.category = category;
    }

    @Override
    public void validate() {
        checkNameConstraints();
        checkDescriptionConstraints();
        checkSlugConstraints();
    }

    private void checkNameConstraints() {
        if (category.getName() == null || category.getName().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("name")));
            return;
        }
        if (category.getName().trim().length() < MINIMUM_LENGTH ||
                category.getName().trim().length() > MAXIMUM_LENGTH) {
            this.validationHandler().append(new Error(lengthBetween("name", MINIMUM_LENGTH, MAXIMUM_LENGTH)));
        }
    }

    private void checkDescriptionConstraints() {
        if (category.getDescription() != null && category.getDescription().isBlank()) {
            this.validationHandler().append(new Error("'description' should not be blank"));
            return;
        }
        if (category.getDescription() != null && category.getDescription().trim().length() > MAXIMUM_LENGTH) {
            this.validationHandler().append(new Error(lengthBetween("description", 0, MAXIMUM_DESCRIPTION_LENGTH)));
        }
    }

    private void checkSlugConstraints() {
        if (category.getSlug() == null || category.getSlug().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("slug")));
            return;
        }
        if (category.getSlug().trim().length() < MINIMUM_LENGTH ||
                category.getSlug().trim().length() > MAXIMUM_LENGTH) {
            this.validationHandler().append(new Error(lengthBetween("slug", MINIMUM_LENGTH, MAXIMUM_LENGTH)));
        }
    }
}
