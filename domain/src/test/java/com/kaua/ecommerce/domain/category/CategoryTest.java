package com.kaua.ecommerce.domain.category;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoryTest {

    @Test
    void givenAValidValuesWithDescription_whenCallNewCategory_shouldReturnACategoryCreated() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final var aIsRoot = true;

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        Assertions.assertNotNull(aCategory);
        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertEquals(aDescription, aCategory.getDescription());
        Assertions.assertEquals(aSlug, aCategory.getSlug());
        Assertions.assertEquals(aIsRoot, aCategory.isRoot());
        Assertions.assertEquals(0, aCategory.getSubCategories().size());
        Assertions.assertNotNull(aCategory.getCreatedAt());
        Assertions.assertNotNull(aCategory.getUpdatedAt());

        Assertions.assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesWithoutDescription_whenCallNewCategory_shouldReturnACategoryCreated() {
        final var aName = "Category Name";
        final String aDescription = null;
        final var aSlug = "category-name";
        final var aIsRoot = true;

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        Assertions.assertNotNull(aCategory);
        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertNull(aCategory.getDescription());
        Assertions.assertEquals(aSlug, aCategory.getSlug());
        Assertions.assertEquals(aIsRoot, aCategory.isRoot());
        Assertions.assertEquals(0, aCategory.getSubCategories().size());
        Assertions.assertNotNull(aCategory.getCreatedAt());
        Assertions.assertNotNull(aCategory.getUpdatedAt());

        Assertions.assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testCategoryIdEqualsAndHashCode() {
        final var aCategoryId = CategoryID.from("123456789");
        final var anotherCategoryId = CategoryID.from("123456789");

        Assertions.assertTrue(aCategoryId.equals(anotherCategoryId));
        Assertions.assertTrue(aCategoryId.equals(aCategoryId));
        Assertions.assertFalse(aCategoryId.equals(null));
        Assertions.assertFalse(aCategoryId.equals(""));
        Assertions.assertEquals(aCategoryId.hashCode(), anotherCategoryId.hashCode());
    }

    @Test
    void givenAnInvalidNullName_whenCallNewCategory_shouldReturnDomainException() {
        final String aName = null;
        final String aDescription = null;
        final var aSlug = "category-name";
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidBlankName_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = " ";
        final String aDescription = null;
        final var aSlug = "category-name";
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = "ca ";
        final String aDescription = null;
        final var aSlug = "category-name";
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = RandomStringUtils.generateValue(256);
        final String aDescription = null;
        final var aSlug = "category-name";
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidBlankDescription_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = "Category Name";
        final var aDescription = " ";
        final var aSlug = "category-name";
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be blank";

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan255_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = "Category Name";
        final var aDescription = RandomStringUtils.generateValue(256);
        final var aSlug = "category-name";
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidBlankSlug_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = " ";
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("slug");

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidNullSlug_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final String aSlug = null;
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("slug");

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidSlugLengthLessThan3_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = "Category Name";
        final String aDescription = null;
        final var aSlug = "ca ";
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("slug", 3, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidSlugLengthMoreThan255_whenCallNewCategory_shouldReturnDomainException() {
        final var aName = "Category Name";
        final String aDescription = null;
        final var aSlug = RandomStringUtils.generateValue(256);
        final var aIsRoot = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("slug", 3, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidSubCategoriesLengthMoreThan5_whenCallAddCategory_shouldReturnDomainException() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final var aIsRoot = true;
        final var aSubCategories = Fixture.Categories.makeSubCategories(6);

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("subCategories", 0, 5);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );
        aCategory.addSubCategories(aSubCategories);

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAValidSubCategories_whenCallAddCategory_shouldReturnACategoryWithSubCategories() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final var aIsRoot = true;
        final var aSubCategories = Fixture.Categories.makeSubCategories(5);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aIsRoot
        );
        aCategory.addSubCategories(aSubCategories);

        Assertions.assertNotNull(aCategory);
        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertEquals(aDescription, aCategory.getDescription());
        Assertions.assertEquals(aSlug, aCategory.getSlug());
        Assertions.assertEquals(aIsRoot, aCategory.isRoot());
        Assertions.assertEquals(5, aCategory.getSubCategories().size());
        Assertions.assertNotNull(aCategory.getCreatedAt());
        Assertions.assertNotNull(aCategory.getUpdatedAt());

        Assertions.assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));
    }
}
