package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@UnitTest
public class ProductImageRelationIdTest {

    @Test
    void givenAProductImageRelationId_whenCallEqualsAndHashCode_thenReturnTrue() {
        final var aProductImageId = ProductImageRelationId.from("1", "2");

        Assertions.assertTrue(aProductImageId.equals(aProductImageId));
        Assertions.assertNotNull(aProductImageId.hashCode());
    }

    @Test
    void givenAProductImageRelationId_whenCallEqualsWithNullAndNotSameClass_thenReturnFalse() {
        final var aProductImageId = ProductImageRelationId.from("1", "2");

        Assertions.assertFalse(aProductImageId.equals(null));
        Assertions.assertFalse(aProductImageId.equals("NotProductImageRelationIdClass"));
    }

    @Test
    void givenATwoProductImageRelationId_whenCallEquals_shouldReturnTrue() {
        final var aProductImageIdOne = ProductImageRelationId.from("1", "2");
        final var aProductImageIdTwo = ProductImageRelationId.from("1", "2");

        Assertions.assertTrue(aProductImageIdOne.equals(aProductImageIdTwo));
    }

    @Test
    void givenAProductImageRelationId_whenCallEqualsWithDifferentProductId_shouldReturnFalse() {
        final var aProductImageIdOne = ProductImageRelationId.from("1", "2");
        final var aProductImageIdTwo = ProductImageRelationId.from("2", "2");

        Assertions.assertFalse(aProductImageIdOne.equals(aProductImageIdTwo));
    }

    @Test
    void givenAProductImageRelationId_whenCallEqualsWithDifferentImageId_shouldReturnFalse() {
        final var aProductImageIdOne = ProductImageRelationId.from("1", "3");
        final var aProductImageIdTwo = ProductImageRelationId.from("1", "2");

        Assertions.assertFalse(aProductImageIdOne.equals(aProductImageIdTwo));
    }

    @Test
    void givenATwoProductImageRelationId_whenCallEqualWithDifferentValues_shouldReturnFalse() {
        final var aProductImageIdOne = ProductImageRelationId.from("1", "2");
        final var aProductImageIdTwo = ProductImageRelationId.from("2", "3");

        Assertions.assertFalse(aProductImageIdOne.equals(aProductImageIdTwo));

    }
}