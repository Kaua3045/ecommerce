package com.kaua.ecommerce.domain.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductSizeTest {

    @Test
    void givenAValidValues_whenCallWith_shouldBeInstantiateProductSize() {
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var aProductSize = ProductSize.with(aSize, aWeight, aHeight, aWidth, aLength);

        Assertions.assertNotNull(aProductSize.getId());
        Assertions.assertEquals(aSize, aProductSize.getSize());
        Assertions.assertEquals(aWeight, aProductSize.getWeight());
        Assertions.assertEquals(aHeight, aProductSize.getHeight());
        Assertions.assertEquals(aWidth, aProductSize.getWidth());
        Assertions.assertEquals(aLength, aProductSize.getLength());
    }

    @Test
    void givenAValidValuesWithId_whenCallWith_shouldBeInstantiateProductSize() {
        final var aId = "1";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var aProductSize = ProductSize.with(aId, aSize, aWeight, aHeight, aWidth, aLength);

        Assertions.assertEquals(aId, aProductSize.getId());
        Assertions.assertEquals(aSize, aProductSize.getSize());
        Assertions.assertEquals(aWeight, aProductSize.getWeight());
        Assertions.assertEquals(aHeight, aProductSize.getHeight());
        Assertions.assertEquals(aWidth, aProductSize.getWidth());
        Assertions.assertEquals(aLength, aProductSize.getLength());
    }

    @Test
    void givenAnInvalidIdAndSize_whenCallWith_shouldThrowNullPointerException() {
        Assertions.assertThrows(NullPointerException.class,
                () -> ProductSize.with(null, "M", 0.5, 0.5, 0.5, 0.5));

        Assertions.assertThrows(NullPointerException.class,
                () -> ProductSize.with("1", null, 0.5, 0.5, 0.5, 0.5));
    }
}
