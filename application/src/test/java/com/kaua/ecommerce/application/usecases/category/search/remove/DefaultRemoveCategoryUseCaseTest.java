package com.kaua.ecommerce.application.usecases.category.search.remove;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.category.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

public class DefaultRemoveCategoryUseCaseTest extends UseCaseTest {

    @Mock
    private SearchGateway<Category> categorySearchGateway;

    @InjectMocks
    private DefaultRemoveCategoryUseCase useCase;

    @Test
    void givenAValidCategoryId_whenCallRemoveCategory_shouldBeOk() {
        final var aCategory = Category.newCategory(
                "Category test",
                "Category test description",
                "category-test",
                null);

        final var aCommand = RemoveCategoryCommand.with(aCategory.getId().getValue(), null);

        Mockito.when(categorySearchGateway.findById(aCategory.getId().getValue())).thenReturn(Optional.of(aCategory));
        Mockito.doNothing().when(categorySearchGateway).deleteById(aCategory.getId().getValue());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categorySearchGateway, Mockito.times(1))
                .findById(aCategory.getId().getValue());
        Mockito.verify(categorySearchGateway, Mockito.times(1))
                .deleteById(aCategory.getId().getValue());
    }

    @Test
    void givenAValidSubCategoryId_whenCallRemoveCategory_shouldBeOk() {
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
        aCategoryRoot.updateSubCategoriesLevel();

        final var aCommand = RemoveCategoryCommand.with(
                aCategoryRoot.getId().getValue(),
                aSubCategory.getId().getValue());

        Mockito.when(categorySearchGateway.findById(aCategoryRoot.getId().getValue())).thenReturn(Optional.of(aCategoryRoot));
        Mockito.when(categorySearchGateway.save(aCategoryRoot)).thenAnswer(returnsFirstArg());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categorySearchGateway, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(categorySearchGateway, Mockito.times(1)).save(aCategoryRoot);
    }

    @Test
    void givenAValidSubSubCategoryId_whenCallRemoveCategory_shouldBeOk() {
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

        final var aCommand = RemoveCategoryCommand.with(
                aCategoryRoot.getId().getValue(),
                aSubSubCategory.getId().getValue());

        Mockito.when(categorySearchGateway.findById(aCategoryRoot.getId().getValue())).thenReturn(Optional.of(aCategoryRoot));
        Mockito.when(categorySearchGateway.save(aCategoryRoot)).thenAnswer(returnsFirstArg());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categorySearchGateway, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(categorySearchGateway, Mockito.times(1)).save(aCategoryRoot);
    }

    @Test
    void givenAnInvalidCategoryId_whenCallDeleteCategory_shouldBeOk() {
        final var aCategoryId = "123";

        final var aCommand = RemoveCategoryCommand.with(aCategoryId, null);

        Mockito.when(categorySearchGateway.findById(aCategoryId)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categorySearchGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(categorySearchGateway, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidSubSubCategoryId_whenCallRemoveCategory_shouldBeOk() {
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

        final var aCommand = RemoveCategoryCommand.with(
                aCategoryRoot.getId().getValue(),
                aSubSubCategoryIdInvalid);

        Mockito.when(categorySearchGateway.findById(aCategoryRoot.getId().getValue())).thenReturn(Optional.of(aCategoryRoot));

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCommand));
        Mockito.verify(categorySearchGateway, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(categorySearchGateway, Mockito.times(0)).save(aCategoryRoot);
    }
}
