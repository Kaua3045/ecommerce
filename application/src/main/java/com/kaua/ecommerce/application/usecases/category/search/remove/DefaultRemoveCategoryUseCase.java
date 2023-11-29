package com.kaua.ecommerce.application.usecases.category.search.remove;

import com.kaua.ecommerce.application.UnitUseCase;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.category.Category;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class DefaultRemoveCategoryUseCase extends UnitUseCase<RemoveCategoryCommand> {

    private final SearchGateway<Category> categorySearchGateway;

    public DefaultRemoveCategoryUseCase(final SearchGateway<Category> categorySearchGateway) {
        this.categorySearchGateway = Objects.requireNonNull(categorySearchGateway);
    }

    @Override
    public void execute(final RemoveCategoryCommand aCommand) {
        final var aCategory = getRootCategory(aCommand.rootCategoryId());

        if (aCommand.subCategoryId().isEmpty()) {
            aCategory.ifPresent(category -> this.categorySearchGateway.deleteById(category.getId().getValue()));
            return;
        }

        aCategory.ifPresent(categoryRoot -> deleteSubCategory(categoryRoot, aCommand));
    }

    private Optional<Category> getRootCategory(final String aRootCategoryId) {
        return this.categorySearchGateway.findById(aRootCategoryId);
    }

    private void deleteSubCategory(final Category aCategory, final RemoveCategoryCommand aCommand) {
        final var iterator = aCategory.getSubCategories().iterator();
        while (iterator.hasNext()) {
            final var aSubCategory = iterator.next();
            if (aSubCategory.getId().getValue().equals(aCommand.subCategoryId().get())) {
                iterator.remove();
                aCategory.updateRemoveSubCategoriesLevel();

                this.categorySearchGateway.save(aCategory);
            } else {
                Iterator<Category> subSubCategoryIterator = aSubCategory.getSubCategories().iterator();
                while (subSubCategoryIterator.hasNext()) {
                    Category aSubSubCategory = subSubCategoryIterator.next();
                    if (aSubSubCategory.getId().getValue().equals(aCommand.subCategoryId().get())) {
                        subSubCategoryIterator.remove();
                        aSubCategory.updateRemoveSubCategoriesLevel();

                        this.categorySearchGateway.save(aCategory);
                    }
                }
            }
        }
    }
}
