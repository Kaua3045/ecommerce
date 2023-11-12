package com.kaua.ecommerce.domain.category;

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
}
