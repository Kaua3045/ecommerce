package com.kaua.ecommerce.domain.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductImageTest {

    @Test
    void givenValidValues_whenCallNewProductImage_shouldBeInstantiateProductImage() {
        final var aCheckSum = "abc";
        final var aName = "product.jpg";
        final var aLocation = "/images/product.jpg";

        final var aProductImage = ProductImage.with(aCheckSum, aName, aLocation);

        Assertions.assertNotNull(aProductImage.id());
        Assertions.assertEquals(aCheckSum, aProductImage.checksum());
        Assertions.assertEquals(aName, aProductImage.name());
        Assertions.assertEquals(aLocation, aProductImage.location());
    }

    @Test
    void givenTwoProductImagesWithSameValues_whenCallEquals_shouldBeReturnTrue() {
        final var aCheckSum = "abc";
        final var aLocation = "/images/product.jpg";

        final var aProductImage = ProductImage.with(aCheckSum, "RANDOM", aLocation);
        final var anotherProductImage = ProductImage.with(aCheckSum, "SIMPLE", aLocation);

        Assertions.assertEquals(aProductImage, anotherProductImage);
        Assertions.assertNotSame(aProductImage, anotherProductImage);
    }

    @Test
    void givenAValidValuesWithId_whenCallWith_shouldBeInstantiateProductImage() {
        final var aId = "abc";
        final var aCheckSum = "abc";
        final var aName = "product.jpg";
        final var aLocation = "/images/product.jpg";

        final var aProductImage = ProductImage.with(aId, aCheckSum, aName, aLocation);

        Assertions.assertEquals(aId, aProductImage.id());
        Assertions.assertEquals(aCheckSum, aProductImage.checksum());
        Assertions.assertEquals(aName, aProductImage.name());
        Assertions.assertEquals(aLocation, aProductImage.location());
    }

    @Test
    void givenInvalidValues_whenCallNewProductImage_shouldBeThrowException() {
        Assertions.assertThrows(NullPointerException.class,
                () -> ProductImage.with(null, "Random", "/images"));

        Assertions.assertThrows(NullPointerException.class,
                () -> ProductImage.with("abc", null, "/images"));

        Assertions.assertThrows(NullPointerException.class,
                () -> ProductImage.with("abc", "Random", null));
    }

    @Test
    void testProductImageEqualsAndHashCode() {
        final var aProductImage = ProductImage.with("abc", "Random", "/images");
        final var anotherProductImage = ProductImage.with("abc", "Random", "/images");

        Assertions.assertTrue(aProductImage.equals(anotherProductImage));
        Assertions.assertTrue(aProductImage.equals(aProductImage));
        Assertions.assertFalse(aProductImage.equals(null));
        Assertions.assertFalse(aProductImage.equals(""));
        Assertions.assertEquals(aProductImage.hashCode(), anotherProductImage.hashCode());
    }

    @Test
    void testProductImageCheckSumNotEquals() {
        final var aProductImage = ProductImage.with("abc", "Random", "/images");
        final var anotherProductImage = ProductImage.with("def", "Random", "/images");

        Assertions.assertFalse(aProductImage.equals(anotherProductImage));
        Assertions.assertTrue(aProductImage.equals(aProductImage));
        Assertions.assertFalse(aProductImage.equals(null));
        Assertions.assertFalse(aProductImage.equals(""));
        Assertions.assertNotEquals(aProductImage.hashCode(), anotherProductImage.hashCode());
    }

    @Test
    void testProductImageLocationNotEquals() {
        final var aProductImage = ProductImage.with("abc", "Random", "/images");
        final var anotherProductImage = ProductImage.with("abc", "Random", "/downloads");

        Assertions.assertFalse(aProductImage.equals(anotherProductImage));
        Assertions.assertTrue(aProductImage.equals(aProductImage));
        Assertions.assertFalse(aProductImage.equals(null));
        Assertions.assertFalse(aProductImage.equals(""));
        Assertions.assertNotEquals(aProductImage.hashCode(), anotherProductImage.hashCode());
    }
}
