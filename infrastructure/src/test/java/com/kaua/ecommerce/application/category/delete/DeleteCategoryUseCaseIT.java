package com.kaua.ecommerce.application.category.delete;

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

        Assertions.assertEquals(1, this.categoryRepository.count());
        Assertions.assertDoesNotThrow(() -> this.deleteCategoryUseCase.execute(aId));
        Assertions.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallsDeleteCategoryUseCase_shouldBeOk() {
        final var aId = "123";

        Assertions.assertEquals(0, this.categoryRepository.count());
        Assertions.assertDoesNotThrow(() -> this.deleteCategoryUseCase.execute(aId));
        Assertions.assertEquals(0, this.categoryRepository.count());
    }
}
