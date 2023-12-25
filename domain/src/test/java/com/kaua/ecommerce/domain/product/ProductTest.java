package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ProductTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewProduct_shouldBeInstantiateProduct() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aQuantity, aCategoryId, aAttributes);

        Assertions.assertNotNull(aProduct.getId());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertTrue(aProduct.getCoverImageUrl().isEmpty());
        Assertions.assertEquals(aCategoryId, aProduct.getCategoryId());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
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
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "GREEN"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aQuantity, aCategoryId, aAttributes);

        Assertions.assertNotNull(aProduct.getId());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertNull(aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertTrue(aProduct.getCoverImageUrl().isEmpty());
        Assertions.assertEquals(aCategoryId, aProduct.getCategoryId());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
        Assertions.assertNotNull(aProduct.getCreatedAt());
        Assertions.assertNotNull(aProduct.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aProduct.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testProductIdEqualsAndHashCode() {
        final var aProductId = ProductID.from("123456789");
        final var anotherProductId = ProductID.from("123456789");

        Assertions.assertTrue(aProductId.equals(anotherProductId));
        Assertions.assertTrue(aProductId.equals(aProductId));
        Assertions.assertFalse(aProductId.equals(null));
        Assertions.assertFalse(aProductId.equals(""));
        Assertions.assertEquals(aProductId.hashCode(), anotherProductId.hashCode());
    }

    @Test
    void givenAValidValues_whenCallWith_shouldBeInstantiateProduct() {
        final var aProductID = "123456789";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCoverImageUrl = ProductImage.with("abc", "product.jpg", "/images/product.jpg");
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aProduct = Product.with(
                aProductID,
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCoverImageUrl,
                aCategoryId.getValue(),
                Set.of(aAttributes),
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertEquals(aProductID, aProduct.getId().getValue());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertEquals(aCoverImageUrl, aProduct.getCoverImageUrl().get());
        Assertions.assertEquals(aCategoryId.getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
        Assertions.assertEquals(aCreatedAt, aProduct.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aProduct.getUpdatedAt());
    }

    @Test
    void givenAValidProduct_whenCallWith_shouldBeInstantiateProduct() {
        final var aProduct = Fixture.Products.tshirt();

        final var aProductWith = Product.with(aProduct);

        Assertions.assertEquals(aProductWith.getId().getValue(), aProduct.getId().getValue());
        Assertions.assertEquals(aProductWith.getName(), aProduct.getName());
        Assertions.assertEquals(aProductWith.getDescription(), aProduct.getDescription());
        Assertions.assertEquals(aProductWith.getPrice(), aProduct.getPrice());
        Assertions.assertEquals(aProductWith.getQuantity(), aProduct.getQuantity());
        Assertions.assertTrue(aProductWith.getCoverImageUrl().isEmpty());
        Assertions.assertEquals(aProductWith.getCategoryId().getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertEquals(aProductWith.getAttributes(), aProduct.getAttributes());
        Assertions.assertEquals(aProductWith.getCreatedAt(), aProduct.getCreatedAt());
        Assertions.assertEquals(aProductWith.getUpdatedAt(), aProduct.getUpdatedAt());
    }

    @Test
    void givenAValidValuesWithNullAttributes_whenCallWith_shouldBeInstantiateProduct() {
        final var aProductID = "123456789";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCoverImageUrl = ProductImage.with("abc", "product.jpg", "/images/product.jpg");
        final var aCategoryId = CategoryID.from("1");
        final Set<ProductAttributes> aAttributes = null;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aProduct = Product.with(
                aProductID,
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCoverImageUrl,
                aCategoryId.getValue(),
                aAttributes,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertEquals(aProductID, aProduct.getId().getValue());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertEquals(aCoverImageUrl, aProduct.getCoverImageUrl().get());
        Assertions.assertEquals(aCategoryId.getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertTrue(aProduct.getAttributes().isEmpty());
        Assertions.assertEquals(aCreatedAt, aProduct.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aProduct.getUpdatedAt());
    }
}
