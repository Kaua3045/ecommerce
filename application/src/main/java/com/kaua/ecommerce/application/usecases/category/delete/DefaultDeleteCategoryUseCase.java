package com.kaua.ecommerce.application.usecases.category.delete;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.events.CategoryDeletedEvent;

import java.util.Iterator;
import java.util.Objects;

public class DefaultDeleteCategoryUseCase extends DeleteCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultDeleteCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public void execute(final DeleteCategoryCommand aCommand) {
        final var aCategory = this.categoryGateway.findById(aCommand.rootCategoryId());

        if (aCommand.subCategoryId().isEmpty()) {
            aCategory.ifPresent(category -> this.categoryGateway.deleteRootCategoryById(category.getId().getValue()));
            return;
        }

        aCategory.ifPresent(categoryRoot -> deleteSubCategory(categoryRoot, aCommand));
    }

    private void deleteSubCategory(final Category aCategory, final DeleteCategoryCommand aCommand) {
        Iterator<Category> iterator = aCategory.getSubCategories().iterator();
        while (iterator.hasNext()) {
            Category aSubCategory = iterator.next();
            if (aSubCategory.getId().getValue().equals(aCommand.subCategoryId().get())) {
                iterator.remove();
                aCategory.updateRemoveSubCategoriesLevel();

                aCategory.registerEvent(CategoryDeletedEvent
                        .from(aCommand.rootCategoryId(), aSubCategory.getId().getValue()));

                this.categoryGateway.update(aCategory);
                this.categoryGateway.deleteById(aSubCategory.getId().getValue());
            } else {
                Iterator<Category> subSubCategoryIterator = aSubCategory.getSubCategories().iterator();
                while (subSubCategoryIterator.hasNext()) {
                    Category aSubSubCategory = subSubCategoryIterator.next();
                    if (aSubSubCategory.getId().getValue().equals(aCommand.subCategoryId().get())) {
                        subSubCategoryIterator.remove();
                        aSubCategory.updateRemoveSubCategoriesLevel();

                        aSubCategory.registerEvent(CategoryDeletedEvent
                                .from(aCommand.rootCategoryId(), aSubSubCategory.getId().getValue()));

                        this.categoryGateway.update(aSubCategory);
                        this.categoryGateway.deleteById(aSubSubCategory.getId().getValue());
                    }
                }
            }
        }
    }
}
