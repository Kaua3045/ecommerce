package com.kaua.ecommerce.domain.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ProductAttributesTest {

    @ParameterizedTest
    @CsvSource({
            "Red, m, 0.5, 0.5, 0.5, 0.5, Product Name",
            "Blue, L, 0.5, 0.5, 0.5, 0.5, Camiseta Azul",
            "Black, XL, 0.5, 0.5, 0.5, 0.5, Camiseta Preta",
            "White, XXL, 0.5, 0.5, 0.5, 0.5, Camiseta Branca",
            "Yellow, M, 0.5, 0.5, 0.5, 0.5, Visionary Pro Gaming Mouse",
            "Black, XL, 1, 3, 2, 1, PixelPerfect 4K Monitor",
            "White, M, 0.5, 0.5, 0.5, 0.5, ProTech Security Camera System",
            "Green, L, 0.5, 0.5, 0.5, 0.5, RoboGuard Home Security System",
            "Red, XXL, 0.5, 0.5, 0.5, 0.5, NeoTrack 2000",
            "Blue, M, 0.5, 0.5, 0.5, 0.5, MegaFlex Plus",
            "Purple, L, 0.5, 0.5, 0.5, 0.5, FlexiSpot Standing Desk",
            "Black, XL, 0.5, 0.5, 0.5, 0.5, HyperGlide 7",
            "Dark Blue, XXL, 0.5, 0.5, 0.5, 0.5, EnergeticFocus 360",
            "Black, XL, 10.0, 50.0, 55.5, 30.0, Xbox Series X",
            "White, L, 10.0, 50.0, 55.5, 30.0, Xbox Series S",
            "White, XL, 10.0, 50.0, 55.5, 30.0, PlayStation 5",
            "White, XL, 10.0, 50.0, 55.5, 30.0, PlayStation 5 Digital",
            "Red, M, 10.0, 50.0, 55.5, 30.0, Nintendo Switch",
    })
    void givenAValidValues_whenCallCreate_shouldBeInstantiateProductAttributes(
            final String aColorName,
            final String aSizeName,
            final double aWeight,
            final double aHeight,
            final double aWidth,
            final double aDepth,
            final String aProductName
    ) {
        final var aColor = ProductColor.with(aColorName);
        final var aSize = ProductSize.with(aSizeName, aWeight, aHeight, aWidth, aDepth);

        final var aProductAttributes = ProductAttributes.create(aColor, aSize, aProductName);

        Assertions.assertEquals(aColor, aProductAttributes.color());
        Assertions.assertEquals(aSize, aProductAttributes.size());
        Assertions.assertNotNull(aProductAttributes.sku());

        System.out.println(aProductAttributes.sku());
    }

    @Test
    void givenAValidValues_whenCallWith_shouldBeInstantiateProductAttributes() {
        final var aColor = ProductColor.with("1", "Red");
        final var aSize = ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5);
        final var aSku = "1-PRO-RED-M";

        final var aProductAttributes = ProductAttributes.with(aColor, aSize, aSku);

        Assertions.assertEquals(aColor, aProductAttributes.color());
        Assertions.assertEquals(aSize, aProductAttributes.size());
        Assertions.assertEquals(aSku, aProductAttributes.sku());
    }

    @Test
    public void testEquals_SameObject() {
        ProductAttributes attributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                "Product Name"
        );
        Assertions.assertTrue(attributes.equals(attributes));
    }

    @Test
    public void testEquals_DifferentAttributes() {
        ProductAttributes attributes1 = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                "Product Name"
        );
        ProductAttributes attributes2 = ProductAttributes.create(
                ProductColor.with("1", "Blue"),
                ProductSize.with("1", "S", 0.5, 0.5, 0.5, 0.5),
                "Product Name"
        );
        Assertions.assertFalse(attributes1.equals(attributes2));
    }

    @Test
    public void testEquals_SameObjects() {
        final var aColor = ProductColor.with("1", "Red");
        final var aSize = ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5);
        final var aSku = "1-PRO-RED-M";
        ProductAttributes attributes1 = ProductAttributes.with(aColor, aSize, aSku);
        ProductAttributes attributes2 = ProductAttributes.with(aColor, aSize, aSku);

        Assertions.assertTrue(attributes1.equals(attributes1));
        Assertions.assertTrue(attributes1.equals(attributes2));
    }

    @Test
    public void testEquals_NullComparison() {
        ProductAttributes attributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                "Product Name"
        );
        Assertions.assertFalse(attributes.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        ProductAttributes attributes = ProductAttributes.create(
                ProductColor.with("1", "Red"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                "Product Name"
        );
        Assertions.assertFalse(attributes.equals("Not a ProductAttributes object"));
    }

    @Test
    public void testHashCode_SameObjects() {
        final var aColor = ProductColor.with("1", "Red");
        final var aSize = ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5);
        final var aSku = "1-PRO-RED-M";
        ProductAttributes attributes1 = ProductAttributes.with(aColor, aSize, aSku);
        ProductAttributes attributes2 = ProductAttributes.with(aColor, aSize, aSku);

        Assertions.assertEquals(attributes1.hashCode(), attributes2.hashCode());
    }
}
