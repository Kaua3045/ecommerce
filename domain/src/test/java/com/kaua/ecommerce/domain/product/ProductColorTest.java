package com.kaua.ecommerce.domain.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductColorTest {

    @Test
    void givenAValidColor_whenCallWith_shouldBeInstantiateProductColor() {
        final var aColor = "Red";
        final var aProductColor = ProductColor.with(aColor);

        Assertions.assertEquals(aColor.toUpperCase(), aProductColor.getColor());
        Assertions.assertNotNull(aProductColor.getId());
    }

    @Test
    void givenAValidColorAndId_whenCallWith_shouldBeInstantiateProductColor() {
        final var aColor = "Red";
        final var aId = "1";
        final var aProductColor = ProductColor.with(aId, aColor);

        Assertions.assertEquals(aColor.toUpperCase(), aProductColor.getColor());
        Assertions.assertEquals(aId, aProductColor.getId());
    }

    @Test
    void givenAnInvalidIdAndColor_whenCallWith_shouldThrowNullPointerException() {
        Assertions.assertThrows(NullPointerException.class,
                () -> ProductColor.with(null, "red"));

        Assertions.assertThrows(NullPointerException.class,
                () -> ProductColor.with("1", null));
    }
}
