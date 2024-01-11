package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.utils.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductImageResourceTest {

    @Test
    void givenAValidParams_whenCallProductImageResourceWith_shouldInstantiate() {
        final var aResource = Resource.with(
                "content".getBytes(),
                "image/png",
                "image.png"
        );

        final var actualProductImageResource = ProductImageResource.with(
                aResource,
                ProductImageType.COVER
        );

        Assertions.assertNotNull(actualProductImageResource);
        Assertions.assertEquals(aResource, actualProductImageResource.resource());
        Assertions.assertEquals(ProductImageType.COVER, actualProductImageResource.type());
    }

    @Test
    void testProductImageResourceIsEquals() {
        final var aProductImageResource = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image.png"
                ),
                ProductImageType.COVER
        );
        final var anotherProductImageResource = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image.png"
                ),
                ProductImageType.COVER
        );

        Assertions.assertTrue(aProductImageResource.equals(anotherProductImageResource));
        Assertions.assertTrue(aProductImageResource.equals(aProductImageResource));
        Assertions.assertFalse(aProductImageResource.equals(null));
        Assertions.assertFalse(aProductImageResource.equals(""));
    }

    @Test
    void testProductImageResourceIsNotEquals() {
        final var aProductImageResource = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image.png"
                ),
                ProductImageType.COVER
        );
        final var anotherProductImageResource = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image.png"
                ),
                ProductImageType.GALLERY
        );

        Assertions.assertFalse(aProductImageResource.equals(anotherProductImageResource));
        Assertions.assertTrue(aProductImageResource.equals(aProductImageResource));
        Assertions.assertFalse(aProductImageResource.equals(null));
        Assertions.assertFalse(aProductImageResource.equals(""));
    }

    @Test
    void testProductImageResourceHashCode() {
        final var aProductImageResource = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image.png"
                ),
                ProductImageType.COVER
        );
        final var anotherProductImageResource = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image.png"
                ),
                ProductImageType.COVER
        );

        Assertions.assertNotEquals(aProductImageResource.hashCode(), anotherProductImageResource.hashCode());
    }
}
