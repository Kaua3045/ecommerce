package com.kaua.ecommerce.domain.category;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class CategoryTest {

    @Test
    void givenAValidValuesWithDescription_whenCallNewCategory_shouldReturnACategoryCreated() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );

        Assertions.assertNotNull(aCategory);
        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertEquals(aDescription, aCategory.getDescription());
        Assertions.assertEquals(aSlug, aCategory.getSlug());
        Assertions.assertTrue(aCategory.getParent().isEmpty());
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
        final Category aParent = null;

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );

        Assertions.assertNotNull(aCategory);
        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertNull(aCategory.getDescription());
        Assertions.assertEquals(aSlug, aCategory.getSlug());
        Assertions.assertTrue(aCategory.getParent().isEmpty());
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be blank";

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("slug");

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("slug");

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("slug", 3, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
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
        final Category aParent = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("slug", 3, 255);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aCategory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAnInvalidSubCategoriesLengthMoreThan5_whenCallAddCategories_shouldThrowDomainException() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("subCategories", 0, 5);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );
        final var aSubCategories = Fixture.Categories.makeSubCategories(6, aCategory);
        aCategory.addSubCategories(aSubCategories);

        final var aExpectedException = Assertions.assertThrows(DomainException.class,
                aCategory::updateSubCategoriesLevel);

        Assertions.assertEquals(expectedErrorMessage, aExpectedException.getErrors().get(0).message());
    }

    @Test
    void givenACategoryWith5SubCategories_whenCallAddCategories_shouldThrowDomainException() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("subCategories", 0, 5);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );
        aCategory.addSubCategories(Fixture.Categories.makeSubCategories(5, aCategory));
        Assertions.assertDoesNotThrow(aCategory::updateSubCategoriesLevel);

        final var aSubCategories = Fixture.Categories.makeSubCategories(6, aCategory);
        aCategory.addSubCategories(aSubCategories);

        final var aExpectedException = Assertions.assertThrows(DomainException.class,
                aCategory::updateSubCategoriesLevel);

        Assertions.assertEquals(expectedErrorMessage, aExpectedException.getErrors().get(0).message());
    }

    @Test
    void givenAValidSubCategories_whenCallAddCategories_shouldReturnACategoryWithSubCategories() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );
        final var aSubCategories = Fixture.Categories.makeSubCategories(5, aCategory);

        aCategory.addSubCategories(aSubCategories);

        Assertions.assertDoesNotThrow(aCategory::updateSubCategoriesLevel);
        Assertions.assertNotNull(aCategory);
        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertEquals(aDescription, aCategory.getDescription());
        Assertions.assertEquals(aSlug, aCategory.getSlug());
        Assertions.assertTrue(aCategory.getParent().isEmpty());
        Assertions.assertEquals(5, aCategory.getSubCategories().size());
        Assertions.assertNotNull(aCategory.getCreatedAt());
        Assertions.assertNotNull(aCategory.getUpdatedAt());

        Assertions.assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValues_whenCallWith_shouldReturnCategoryObject() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );
        final var aSubCategories = Fixture.Categories.makeSubCategories(5, aCategory);

        final var aCategoryWith = Category.with(
                aCategory.getId().getValue(),
                aName,
                aDescription,
                aSlug,
                aParent,
                aSubCategories,
                aCategory.getLevel(),
                aCategory.getCreatedAt(),
                aCategory.getUpdatedAt()
        );

        Assertions.assertNotNull(aCategory);
        Assertions.assertEquals(aCategory.getId().getValue(), aCategoryWith.getId().getValue());
        Assertions.assertEquals(aCategory.getName(), aCategoryWith.getName());
        Assertions.assertEquals(aCategory.getDescription(), aCategoryWith.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), aCategoryWith.getSlug());
        Assertions.assertTrue(aCategoryWith.getParent().isEmpty());
        Assertions.assertEquals(5, aCategoryWith.getSubCategories().size());
        Assertions.assertEquals(aCategory.getLevel(), aCategoryWith.getLevel());
        Assertions.assertEquals(aCategory.getCreatedAt(), aCategoryWith.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), aCategoryWith.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesButNullSubCategories_whenCallWith_shouldReturnCategoryObject() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );
        final Set<Category> aSubCategories = null;

        final var aCategoryWith = Category.with(
                aCategory.getId().getValue(),
                aName,
                aDescription,
                aSlug,
                aParent,
                aSubCategories,
                aCategory.getLevel(),
                aCategory.getCreatedAt(),
                aCategory.getUpdatedAt()
        );

        Assertions.assertNotNull(aCategory);
        Assertions.assertEquals(aCategory.getId().getValue(), aCategoryWith.getId().getValue());
        Assertions.assertEquals(aCategory.getName(), aCategoryWith.getName());
        Assertions.assertEquals(aCategory.getDescription(), aCategoryWith.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), aCategoryWith.getSlug());
        Assertions.assertTrue(aCategoryWith.getParent().isEmpty());
        Assertions.assertEquals(0, aCategoryWith.getSubCategories().size());
        Assertions.assertEquals(aCategory.getLevel(), aCategoryWith.getLevel());
        Assertions.assertEquals(aCategory.getCreatedAt(), aCategoryWith.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), aCategoryWith.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAnInvalidSubCategoryLengthMoreThan5_whenCallAddCategory_shouldThrowDomainException() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("subCategories", 0, 5);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );
        final var aSubCategories = Fixture.Categories.makeSubCategories(6, aCategory);
        aSubCategories.forEach(aCategory::addSubCategory);

        final var aExpectedException = Assertions.assertThrows(DomainException.class,
                aCategory::updateSubCategoriesLevel);

        Assertions.assertEquals(expectedErrorMessage, aExpectedException.getErrors().get(0).message());
    }

    @Test
    void givenACategoryWith5SubCategory_whenCallAddCategory_shouldThrowDomainException() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("subCategories", 0, 5);

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );
        Fixture.Categories.makeSubCategories(5, aCategory).forEach(aCategory::addSubCategory);
        Assertions.assertDoesNotThrow(aCategory::updateSubCategoriesLevel);

        final var aSubCategories = Fixture.Categories.makeSubCategories(6, aCategory);
        aSubCategories.forEach(aCategory::addSubCategory);

        final var aExpectedException = Assertions.assertThrows(DomainException.class,
                aCategory::updateSubCategoriesLevel);

        Assertions.assertEquals(expectedErrorMessage, aExpectedException.getErrors().get(0).message());
    }

    @Test
    void givenAValidSubCategory_whenCallAddCategory_shouldReturnACategoryWithSubCategories() {
        final var aName = "Category Name";
        final var aDescription = "Category Description";
        final var aSlug = "category-name";
        final Category aParent = null;

        final var aCategory = Category.newCategory(
                aName,
                aDescription,
                aSlug,
                aParent
        );
        final var aSubCategories = Fixture.Categories.makeSubCategories(5, aCategory);

        aSubCategories.forEach(aCategory::addSubCategory);

        Assertions.assertDoesNotThrow(aCategory::updateSubCategoriesLevel);
        Assertions.assertNotNull(aCategory);
        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertEquals(aDescription, aCategory.getDescription());
        Assertions.assertEquals(aSlug, aCategory.getSlug());
        Assertions.assertTrue(aCategory.getParent().isEmpty());
        Assertions.assertEquals(5, aCategory.getSubCategories().size());
        Assertions.assertNotNull(aCategory.getCreatedAt());
        Assertions.assertNotNull(aCategory.getUpdatedAt());

        Assertions.assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));
    }
}
