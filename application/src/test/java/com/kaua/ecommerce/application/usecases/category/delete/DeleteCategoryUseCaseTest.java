package com.kaua.ecommerce.application.usecases.category.delete;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class DeleteCategoryUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }


    @Test
    void givenAValidCategoryId_whenCallDeleteCategory_shouldBeOk() {
        final var aCategory = Category.newCategory(
                "Category test",
                "Category test description",
                "category-test",
                null);

        Mockito.doNothing().when(categoryGateway).deleteById(aCategory.getId().getValue());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCategory.getId().getValue()));
        Mockito.verify(categoryGateway, Mockito.times(1))
                .deleteById(aCategory.getId().getValue());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallDeleteCategory_shouldBeOk() {
        final var aCategoryId = "123";

        Mockito.doNothing().when(categoryGateway).deleteById(aCategoryId);

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCategoryId));
        Mockito.verify(categoryGateway, Mockito.times(1)).deleteById(aCategoryId);
    }
}
