package com.kaua.ecommerce.application.usecases.category.search.save;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.eq;

public class DefaultSaveCategoryUseCaseTest extends UseCaseTest {

    @Mock
    private SearchGateway<Category> categorySearchGateway;

    @InjectMocks
    private DefaultSaveCategoryUseCase useCase;

    @Test
    void givenAValidCategoryWithoutSubCategories_whenCallSave_shouldPersistCategory() {
        final var aCategory = Fixture.Categories.tech();

        Mockito.when(categorySearchGateway.save(Mockito.any())).thenAnswer(returnsFirstArg());

        this.useCase.execute(aCategory);

        Mockito.verify(categorySearchGateway, Mockito.times(1)).save(eq(aCategory));
    }

    @Test
    void givenAValidCategoryWithSubCategories_whenCallSave_shouldPersistCategory() {
        final var aCategory = Fixture.Categories.televisionsWithSubCategories();

        Mockito.when(categorySearchGateway.save(Mockito.any())).thenAnswer(returnsFirstArg());

        this.useCase.execute(aCategory);

        Mockito.verify(categorySearchGateway, Mockito.times(1)).save(eq(aCategory));
    }

    @Test
    void givenAnInvalidName_whenCallSave_shouldThrowsDomainException() {
        final var aCategory = Category.newCategory(
                " ",
                "Tech category",
                "tech",
                null
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var actualError = Assertions.assertThrows(DomainException.class,
                () -> this.useCase.execute(aCategory));

        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());

        Mockito.verify(categorySearchGateway, Mockito.times(0)).save(eq(aCategory));
    }

    @Test
    void givenAnInvalidNullCategory_whenCallSave_shouldThrowsDomainException() {
        final Category aCategory = null;

        final var expectedErrorMessage = "'aCategory' cannot be null";
        final var expectedErrorCount = 1;

        final var actualError = Assertions.assertThrows(DomainException.class,
                () -> this.useCase.execute(aCategory));

        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());

        Mockito.verify(categorySearchGateway, Mockito.times(0)).save(eq(aCategory));
    }
}
