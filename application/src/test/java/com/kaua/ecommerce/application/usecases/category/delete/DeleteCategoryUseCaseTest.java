package com.kaua.ecommerce.application.usecases.category.delete;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

public class DeleteCategoryUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @Test
    void givenAValidCategoryId_whenCallDeleteCategory_shouldBeOk() {
        final var aCategory = Category.newCategory(
                "Category test",
                "Category test description",
                "category-test",
                null);

        final var aCommand = DeleteCategoryCommand.with(aCategory.getId().getValue(), null);

        Mockito.when(categoryGateway.findById(aCategory.getId().getValue())).thenReturn(Optional.of(aCategory));
        Mockito.doNothing().when(categoryGateway).deleteRootCategoryById(aCategory.getId().getValue());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categoryGateway, Mockito.times(1))
                .findById(aCategory.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1))
                .deleteRootCategoryById(aCategory.getId().getValue());
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

        final var aCommand = DeleteCategoryCommand.with(
                aCategoryRoot.getId().getValue(),
                aSubCategory.getId().getValue());

        Mockito.when(categoryGateway.findById(aCategoryRoot.getId().getValue())).thenReturn(Optional.of(aCategoryRoot));
        Mockito.when(categoryGateway.update(aCategoryRoot)).thenAnswer(returnsFirstArg());
        Mockito.doNothing().when(categoryGateway).deleteById(aSubCategory.getId().getValue());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categoryGateway, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(categoryGateway, Mockito.times(1)).update(aCategoryRoot);
        Mockito.verify(categoryGateway, Mockito.times(1))
                .deleteById(aSubCategory.getId().getValue());
    }

    @Test
    void givenAValidSubSubCategoryId_whenCallDeleteCategory_shouldBeOk() {
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
        final var aSubSubCategory = Category.newCategory(
                "Sub sub category test",
                "Sub sub category test description",
                "sub-sub-category-test",
                aSubCategory.getId());
        aSubCategory.addSubCategory(aSubSubCategory);
        aSubCategory.updateSubCategoriesLevel();

        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();

        final var aCommand = DeleteCategoryCommand.with(
                aCategoryRoot.getId().getValue(),
                aSubSubCategory.getId().getValue());

        Mockito.when(categoryGateway.findById(aCategoryRoot.getId().getValue())).thenReturn(Optional.of(aCategoryRoot));
        Mockito.when(categoryGateway.update(aSubCategory)).thenAnswer(returnsFirstArg());
        Mockito.doNothing().when(categoryGateway).deleteById(aSubSubCategory.getId().getValue());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categoryGateway, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(categoryGateway, Mockito.times(1)).update(aSubCategory);
        Mockito.verify(categoryGateway, Mockito.times(1))
                .deleteById(aSubSubCategory.getId().getValue());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallDeleteCategory_shouldBeOk() {
        final var aCategoryId = "123";

        final var aCommand = DeleteCategoryCommand.with(aCategoryId, null);

        Mockito.when(categoryGateway.findById(aCategoryId)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(categoryGateway, Mockito.times(0)).deleteById(aCategoryId);
    }

    @Test
    void givenAnInvalidSubSubCategoryId_whenCallDeleteCategory_shouldBeOk() {
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
        final var aSubSubCategory = Category.newCategory(
                "Sub sub category test",
                "Sub sub category test description",
                "sub-sub-category-test",
                aSubCategory.getId());

        final var aSubSubCategoryIdInvalid = "123";

        aSubCategory.addSubCategory(aSubSubCategory);
        aSubCategory.updateSubCategoriesLevel();

        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();

        final var aCommand = DeleteCategoryCommand.with(
                aCategoryRoot.getId().getValue(),
                aSubSubCategoryIdInvalid);

        Mockito.when(categoryGateway.findById(aCategoryRoot.getId().getValue())).thenReturn(Optional.of(aCategoryRoot));

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categoryGateway, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(categoryGateway, Mockito.times(0))
                .deleteById(aSubSubCategoryIdInvalid);
    }
}
