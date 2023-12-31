package com.kaua.ecommerce.infrastructure.product.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unitTest")
public class ProductAttributesIdTest {

    @Test
    void testProductAttributesIdEqualsAndHashCode() {
        final var aProductAttributesId = ProductAttributesId.from("1");
        final var anotherProductAttributesId = ProductAttributesId.from("1");

        Assertions.assertTrue(aProductAttributesId.equals(anotherProductAttributesId));
        Assertions.assertTrue(aProductAttributesId.equals(aProductAttributesId));
        Assertions.assertFalse(aProductAttributesId.equals(null));
        Assertions.assertFalse(aProductAttributesId.equals(""));
        Assertions.assertEquals(aProductAttributesId.hashCode(), anotherProductAttributesId.hashCode());
        Assertions.assertEquals(aProductAttributesId.getProductId(), anotherProductAttributesId.getProductId());
    }
}