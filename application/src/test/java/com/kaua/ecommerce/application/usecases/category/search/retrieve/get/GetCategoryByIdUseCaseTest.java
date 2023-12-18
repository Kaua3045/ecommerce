package com.kaua.ecommerce.application.usecases.category.search.retrieve.get;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class GetCategoryByIdUseCaseTest extends UseCaseTest {

    @Mock
    private SearchGateway<Category> categorySearchGateway;

    @InjectMocks
    private DefaultGetCategoryByIdUseCase defaultGetCategoryByIdUseCase;

    @Test
    void givenAValidId_whenCallGetCategoryByIdUseCase_shouldReturnACategory() {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        Mockito.when(this.categorySearchGateway.findByIdNested(aId))
                .thenReturn(Optional.of(aCategory));

        final var aOutput = this.defaultGetCategoryByIdUseCase.execute(aId);

        Assertions.assertEquals(aCategory.getId().getValue(), aOutput.id());
        Assertions.assertEquals(aCategory.getName(), aOutput.name());
        Assertions.assertEquals(aCategory.getDescription(), aOutput.description());
        Assertions.assertEquals(aCategory.getSlug(), aOutput.slug());
        Assertions.assertNull(aOutput.parentId());
        Assertions.assertEquals(aCategory.getLevel(), aOutput.subCategoriesLevel());
        Assertions.assertEquals(aCategory.getSubCategories().size(), aOutput.subCategories().size());
        Assertions.assertEquals(aCategory.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), aOutput.updatedAt());
    }

    @Test
    void givenAValidId_whenCallGetCategoryByIdUseCase_shouldReturnACategoryWithSubCategories() {
        final var aCategory = Fixture.Categories.televisionsWithSubCategories();
        final var aId = aCategory.getId().getValue();

        Mockito.when(this.categorySearchGateway.findByIdNested(aId))
                .thenReturn(Optional.of(aCategory));

        final var aOutput = this.defaultGetCategoryByIdUseCase.execute(aId);

        Assertions.assertEquals(aCategory.getId().getValue(), aOutput.id());
        Assertions.assertEquals(aCategory.getName(), aOutput.name());
        Assertions.assertEquals(aCategory.getDescription(), aOutput.description());
        Assertions.assertEquals(aCategory.getSlug(), aOutput.slug());
        Assertions.assertNull(aOutput.parentId());
        Assertions.assertEquals(aCategory.getLevel(), aOutput.subCategoriesLevel());
        Assertions.assertEquals(aCategory.getSubCategories().size(), aOutput.subCategories().size());
        Assertions.assertEquals(aCategory.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), aOutput.updatedAt());
    }

    @Test
    void givenAnInvalidId_whenCallGetCategoryByIdUseCase_shouldThrowNotFoundException() {
        final var aId = CategoryID.unique().getValue();

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aId);

        Mockito.when(this.categorySearchGateway.findByIdNested(aId))
                .thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(NotFoundException.class,
                () -> this.defaultGetCategoryByIdUseCase.execute(aId));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());
    }
}
