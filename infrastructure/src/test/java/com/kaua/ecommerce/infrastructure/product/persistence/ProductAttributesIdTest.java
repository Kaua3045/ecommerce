package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@UnitTest
public class ProductAttributesIdTest {

    @Test
    void givenAProductAttributesId_whenCallEqualsAndHashCode_thenReturnTrue() {
        final var aProductAttributesId = ProductAttributesId.from("1", "2", "3");

        Assertions.assertTrue(aProductAttributesId.equals(aProductAttributesId));
        Assertions.assertNotNull(aProductAttributesId.hashCode());
    }

    @Test
    void givenAProductAttributesId_whenCallEqualsWithNullAndNotSameClass_thenReturnFalse() {
        final var aProductAttributesId = ProductAttributesId.from("1", "2", "3");

        Assertions.assertFalse(aProductAttributesId.equals(null));
        Assertions.assertFalse(aProductAttributesId.equals("NotProductAttributesRelationIdClass"));
    }

    @Test
    void givenATwoProductAttributesId_whenCallEquals_shouldReturnTrue() {
        final var aProductAttributesIdOne = ProductAttributesId.from("1", "2", "3");
        final var aProductAttributesIdTwo = ProductAttributesId.from("1", "2", "3");

        Assertions.assertTrue(aProductAttributesIdOne.equals(aProductAttributesIdTwo));
    }

    @Test
    void givenAProductAttributesId_whenCallEqualsWithDifferentProductId_shouldReturnFalse() {
        final var aProductAttributesIdOne = ProductAttributesId.from("1", "2", "3");
        final var aProductAttributesIdTwo = ProductAttributesId.from("2", "2", "3");

        Assertions.assertFalse(aProductAttributesIdOne.equals(aProductAttributesIdTwo));
    }

    @Test
    void givenAProductAttributesId_whenCallEqualsWithDifferentColorId_shouldReturnFalse() {
        final var aProductAttributesIdOne = ProductAttributesId.from("1", "3", "4");
        final var aProductAttributesIdTwo = ProductAttributesId.from("1", "2", "4");

        Assertions.assertFalse(aProductAttributesIdOne.equals(aProductAttributesIdTwo));
    }

    @Test
    void givenATwoProductAttributesId_whenCallEqualWithDifferentValues_shouldReturnFalse() {
        final var aProductAttributesIdOne = ProductAttributesId.from("1", "2", "4");
        final var aProductAttributesIdTwo = ProductAttributesId.from("2", "3", "5");

        Assertions.assertFalse(aProductAttributesIdOne.equals(aProductAttributesIdTwo));
    }

    @Test
    void givenAProductAttributesId_whenCallEqualsWithDifferentSizeId_shouldReturnFalse() {
        final var aProductAttributesIdOne = ProductAttributesId.from("1", "2", "4");
        final var aProductAttributesIdTwo = ProductAttributesId.from("1", "2", "3");

        Assertions.assertFalse(aProductAttributesIdOne.equals(aProductAttributesIdTwo));
    }
}