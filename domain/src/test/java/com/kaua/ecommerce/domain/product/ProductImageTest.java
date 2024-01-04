package com.kaua.ecommerce.domain.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductImageTest {

    @Test
    void givenValidValues_whenCallNewProductImage_shouldBeInstantiateProductImage() {
        final var aName = "product.jpg";
        final var aLocation = "/images/product.jpg";
        final var aUrl = "http://localhost:8080/images/product.jpg";

        final var aProductImage = ProductImage.with(aName, aLocation, aUrl);

        Assertions.assertNotNull(aProductImage.id());
        Assertions.assertEquals(aName, aProductImage.name());
        Assertions.assertEquals(aLocation, aProductImage.location());
        Assertions.assertEquals(aUrl, aProductImage.url());
    }

    @Test
    void givenTwoProductImagesWithSameValues_whenCallEquals_shouldBeReturnTrue() {
        final var aLocation = "/images/product.jpg";
        final var aUrl = "http://localhost:8080/images/product.jpg";

        final var aProductImage = ProductImage.with("1", "RANDOM", aLocation, aUrl);
        final var anotherProductImage = ProductImage.with("1", "SIMPLE", aLocation, aUrl);

        Assertions.assertEquals(aProductImage, anotherProductImage);
        Assertions.assertNotSame(aProductImage, anotherProductImage);
    }

    @Test
    void givenAValidValuesWithId_whenCallWith_shouldBeInstantiateProductImage() {
        final var aId = "abc";
        final var aName = "product.jpg";
        final var aLocation = "/images/product.jpg";
        final var aUrl = "http://localhost:8080/images/product.jpg";

        final var aProductImage = ProductImage.with(aId, aName, aLocation, aUrl);

        Assertions.assertEquals(aId, aProductImage.id());
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

        Assertions.assertThrows(NullPointerException.class,
                () -> ProductImage.with("abc", "Random", null));
    }

    @Test
    void testProductImageEqualsAndHashCode() {
        final var aProductImage = ProductImage.with("abc", "Random", "/images", "http://localhost:8080/images");
        final var anotherProductImage = ProductImage.with("abc", "Random", "/images", "http://localhost:8080/images");

        Assertions.assertTrue(aProductImage.equals(anotherProductImage));
        Assertions.assertTrue(aProductImage.equals(aProductImage));
        Assertions.assertFalse(aProductImage.equals(null));
        Assertions.assertFalse(aProductImage.equals(""));
        Assertions.assertEquals(aProductImage.hashCode(), anotherProductImage.hashCode());
    }

    @Test
    void testProductImageIdNotEquals() {
        final var aProductImage = ProductImage.with("abc", "Random", "/images", "http://localhost:8080/images");
        final var anotherProductImage = ProductImage.with("def", "Random", "/images", "http://localhost:8080/images");

        Assertions.assertFalse(aProductImage.equals(anotherProductImage));
        Assertions.assertTrue(aProductImage.equals(aProductImage));
        Assertions.assertFalse(aProductImage.equals(null));
        Assertions.assertFalse(aProductImage.equals(""));
        Assertions.assertNotEquals(aProductImage.hashCode(), anotherProductImage.hashCode());
    }

    @Test
    void testProductImageLocationNotEquals() {
        final var aProductImage = ProductImage.with("abc", "Random", "/images", "http://localhost:8080/images");
        final var anotherProductImage = ProductImage.with("abc", "Random", "/downloads", "http://localhost:8080/images");

        Assertions.assertFalse(aProductImage.equals(anotherProductImage));
        Assertions.assertTrue(aProductImage.equals(aProductImage));
        Assertions.assertFalse(aProductImage.equals(null));
        Assertions.assertFalse(aProductImage.equals(""));
        Assertions.assertNotEquals(aProductImage.hashCode(), anotherProductImage.hashCode());
    }
}
