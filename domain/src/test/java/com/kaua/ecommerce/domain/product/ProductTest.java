package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewProduct_shouldBeInstantiateProduct() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = CategoryID.from("1");

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aQuantity, aCategoryId);

        Assertions.assertNotNull(aProduct.getId());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertTrue(aProduct.getCoverImageUrl().isEmpty());
        Assertions.assertEquals(aCategoryId, aProduct.getCategoryId());
        Assertions.assertNotNull(aProduct.getCreatedAt());
        Assertions.assertNotNull(aProduct.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aProduct.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesWithoutDescription_whenCallNewProduct_shouldBeInstantiateProduct() {
        final var aName = "Product Name";
        final String aDescription = null;
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = CategoryID.from("1");

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aQuantity, aCategoryId);

        Assertions.assertNotNull(aProduct.getId());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertNull(aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertTrue(aProduct.getCoverImageUrl().isEmpty());
        Assertions.assertEquals(aCategoryId, aProduct.getCategoryId());
        Assertions.assertNotNull(aProduct.getCreatedAt());
        Assertions.assertNotNull(aProduct.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aProduct.validate(new ThrowsValidationHandler()));
    }
}
