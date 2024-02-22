package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

public class ProductValidationTest {

    @Test
    void givenInvalidBlankName_whenCallNewProduct_shouldReturnDomainException() {
        final var aName = " ";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        final var aTestValidationHandler = new TestValidationHandler();
        aProduct.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullName_whenCallNewProduct_shouldReturnDomainException() {
        final String aName = null;
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        final var aTestValidationHandler = new TestValidationHandler();
        aProduct.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNameLengthLessThan3_whenCallNewProduct_shouldReturnDomainException() {
        final var aName = "Ab ";
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        final var aTestValidationHandler = new TestValidationHandler();
        aProduct.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNameLengthMoreThan255_whenCallNewProduct_shouldReturnDomainException() {
        final var aName = RandomStringUtils.generateValue(256);
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        final var aTestValidationHandler = new TestValidationHandler();
        aProduct.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidDescriptionLengthMoreThan3000_whenCallNewProduct_shouldReturnDomainException() {
        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(3001);
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 3000);
        final var expectedErrorCount = 1;

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        final var aTestValidationHandler = new TestValidationHandler();
        aProduct.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullPrice_whenCallNewProduct_shouldReturnDomainException() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final BigDecimal aPrice = null;
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("price");
        final var expectedErrorCount = 1;

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        final var aTestValidationHandler = new TestValidationHandler();
        aProduct.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidPriceSmallerOrEqualZero_whenCallNewProduct_shouldReturnDomainException() {
        final var aName = "Product Name";
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(0.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);
        final var expectedErrorCount = 1;

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        final var aTestValidationHandler = new TestValidationHandler();
        aProduct.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidProductAttributesNull_whenCallNewProduct_shouldThrowNullPointerException() {
        final var aName = "Product Name";
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final Set<ProductAttributes> aAttributes = null;

        final var expectedErrorMessage = "'attributes' must not be null";
        final var expectedErrorCount = 1;

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, aAttributes);

        final var aTestValidationHandler = new TestValidationHandler();
        aProduct.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullProductColor_whenCallProductAttributesCreate_shouldReturnDomainException() {
        final var aName = "Product Name";

        final var expectedErrorMessage = "'color' must not be null";

        final var aException = Assertions.assertThrows(NullPointerException.class,
                () -> ProductAttributes.create(null,
                        ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                        aName));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenInvalidNullProductSize_whenCallProductAttributesCreate_shouldThrowNullPointerException() {
        final var aName = "Product Name";

        final var expectedErrorMessage = "'size' must not be null";

        final var aException = Assertions.assertThrows(NullPointerException.class,
                () -> ProductAttributes.create(ProductColor.with("1", "Red"), null,
                        aName));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenInvalidBlankName_whenCallUpdate_shouldReturnDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aName = " ";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(50.0);
        final var aCategoryId = CategoryID.from("1");

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aCategoryId);

        final var aTestValidationHandler = new TestValidationHandler();
        aProductUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullName_whenCallUpdate_shouldReturnDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final String aName = null;
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(50.0);
        final var aCategoryId = CategoryID.from("1");

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aCategoryId);

        final var aTestValidationHandler = new TestValidationHandler();
        aProductUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNameLengthLessThan3_whenCallUpdate_shouldReturnDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aName = "Ab ";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(50.0);
        final var aCategoryId = CategoryID.from("1");

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aCategoryId);

        final var aTestValidationHandler = new TestValidationHandler();
        aProductUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNameLengthMoreThan255_whenCallUpdate_shouldReturnDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(50.0);
        final var aCategoryId = CategoryID.from("1");

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aCategoryId);

        final var aTestValidationHandler = new TestValidationHandler();
        aProductUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidDescriptionLengthMoreThan3000_whenCallUpdate_shouldReturnDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(3001);
        final var aPrice = BigDecimal.valueOf(50.0);
        final var aCategoryId = CategoryID.from("1");

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 3000);
        final var expectedErrorCount = 1;

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aCategoryId);

        final var aTestValidationHandler = new TestValidationHandler();
        aProductUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullPrice_whenCallUpdate_shouldReturnDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final BigDecimal aPrice = null;
        final var aCategoryId = CategoryID.from("1");

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("price");
        final var expectedErrorCount = 1;

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aCategoryId);

        final var aTestValidationHandler = new TestValidationHandler();
        aProductUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidPriceSmallerOrEqualZero_whenCallUpdate_shouldReturnDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aName = "Product Name";
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(0.0);
        final var aCategoryId = CategoryID.from("1");

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);
        final var expectedErrorCount = 1;

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aCategoryId);

        final var aTestValidationHandler = new TestValidationHandler();
        aProductUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }
}
