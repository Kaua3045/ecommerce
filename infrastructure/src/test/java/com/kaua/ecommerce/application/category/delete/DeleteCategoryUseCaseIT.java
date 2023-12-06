package com.kaua.ecommerce.application.category.delete;

import com.kaua.ecommerce.application.usecases.category.delete.DeleteCategoryCommand;
import com.kaua.ecommerce.application.usecases.category.delete.DeleteCategoryUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class DeleteCategoryUseCaseIT {

    @Autowired
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Test
    void givenAValidCategoryId_whenCallsDeleteCategoryUseCase_shouldBeOk() {
        final var aCategory = Fixture.Categories.tech();
        final var aId = this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory)).getId();

        final var aCommand = DeleteCategoryCommand.with(aId, null);

        Assertions.assertEquals(1, this.categoryRepository.count());
        Assertions.assertDoesNotThrow(() -> this.deleteCategoryUseCase.execute(aCommand));
        Assertions.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAValidCategoryIdAndSubCategoryId_whenCallsDeleteCategoryUseCase_shouldBeOk() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory)
                .stream().findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory)).getId();
        final var aSubId = aSubCategory.getId().getValue();

        final var aCommand = DeleteCategoryCommand.with(aId, aSubId);

        Assertions.assertEquals(2, this.categoryRepository.count());
        Assertions.assertDoesNotThrow(() -> this.deleteCategoryUseCase.execute(aCommand));
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallsDeleteCategoryUseCase_shouldBeOk() {
        final var aId = "123";

        final var aCommand = DeleteCategoryCommand.with(aId, null);

        Assertions.assertEquals(0, this.categoryRepository.count());
        Assertions.assertDoesNotThrow(() -> this.deleteCategoryUseCase.execute(aCommand));
        Assertions.assertEquals(0, this.categoryRepository.count());
    }
}
