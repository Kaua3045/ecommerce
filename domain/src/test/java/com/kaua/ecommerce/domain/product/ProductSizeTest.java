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
        final var aDepth = 0.5;

        final var aProductSize = ProductSize.with(aSize, aWeight, aHeight, aWidth, aDepth);

        Assertions.assertNotNull(aProductSize.id());
        Assertions.assertEquals(aSize, aProductSize.size());
        Assertions.assertEquals(aWeight, aProductSize.weight());
        Assertions.assertEquals(aHeight, aProductSize.height());
        Assertions.assertEquals(aWidth, aProductSize.width());
        Assertions.assertEquals(aDepth, aProductSize.depth());
    }

    @Test
    void givenAValidValuesWithId_whenCallWith_shouldBeInstantiateProductSize() {
        final var aId = "1";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var aProductSize = ProductSize.with(aId, aSize, aWeight, aHeight, aWidth, aDepth);

        Assertions.assertEquals(aId, aProductSize.id());
        Assertions.assertEquals(aSize, aProductSize.size());
        Assertions.assertEquals(aWeight, aProductSize.weight());
        Assertions.assertEquals(aHeight, aProductSize.height());
        Assertions.assertEquals(aWidth, aProductSize.width());
        Assertions.assertEquals(aDepth, aProductSize.depth());
    }

    @Test
    void givenAnInvalidIdAndSize_whenCallWith_shouldThrowNullPointerException() {
        Assertions.assertThrows(NullPointerException.class,
                () -> ProductSize.with(null, "M", 0.5, 0.5, 0.5, 0.5));

        Assertions.assertThrows(NullPointerException.class,
                () -> ProductSize.with("1", null, 0.5, 0.5, 0.5, 0.5));
    }
}
