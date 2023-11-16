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
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

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

        Mockito.when(categoryGateway.findById(aCategory.getId().getValue())).thenReturn(Optional.of(aCategory));
        Mockito.doNothing().when(categoryGateway).deleteById(aCategory.getId().getValue());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCategory.getId().getValue()));
        Mockito.verify(categoryGateway, Mockito.times(1))
                .findById(aCategory.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1))
                .deleteById(aCategory.getId().getValue());
    }

    @Test
    void givenAValidSubCategoryId_whenCallDeleteCategory_shouldBeOk() {
        final var aCategoryRoot = Category.newCategory(
                "Category test",
                "Category test description",
                "category-test",
                null);
        final var aSubCategory = Category.newCategory(
                "Sub category test",
                "Sub category test description",
                "sub-category-test",
                aCategoryRoot.getId());
        aCategoryRoot.addSubCategory(aSubCategory);

        Mockito.when(categoryGateway.findById(aSubCategory.getId().getValue())).thenReturn(Optional.of(aSubCategory));
        Mockito.when(categoryGateway.findById(aCategoryRoot.getId().getValue())).thenReturn(Optional.of(aCategoryRoot));
        Mockito.when(categoryGateway.update(aCategoryRoot)).thenAnswer(returnsFirstArg());
        Mockito.doNothing().when(categoryGateway).deleteById(aSubCategory.getId().getValue());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aSubCategory.getId().getValue()));
        Mockito.verify(categoryGateway, Mockito.times(2))
                .findById(Mockito.any());
        Mockito.verify(categoryGateway, Mockito.times(1)).update(aCategoryRoot);
        Mockito.verify(categoryGateway, Mockito.times(1))
                .deleteById(aSubCategory.getId().getValue());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallDeleteCategory_shouldBeOk() {
        final var aCategoryId = "123";

        Mockito.when(categoryGateway.findById(aCategoryId)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCategoryId));
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(categoryGateway, Mockito.times(0)).deleteById(aCategoryId);
    }
}
