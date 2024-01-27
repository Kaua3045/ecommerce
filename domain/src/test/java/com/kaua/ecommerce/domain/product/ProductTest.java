package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

public class ProductTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewProduct_shouldBeInstantiateProduct() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aQuantity, aCategoryId, Set.of(aAttributes));

        Assertions.assertNotNull(aProduct.getId());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertTrue(aProduct.getBannerImage().isEmpty());
        Assertions.assertTrue(aProduct.getImages().isEmpty());
        Assertions.assertEquals(aCategoryId, aProduct.getCategoryId());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
        Assertions.assertNotNull(aProduct.getCreatedAt());
        Assertions.assertNotNull(aProduct.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aProduct.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesWithoutDescription_whenCallNewProduct_shouldBeInstantiateProduct() {
        final var aName = "Product Name";
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aQuantity, aCategoryId, Set.of(aAttributes));

        Assertions.assertNotNull(aProduct.getId());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertNull(aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertTrue(aProduct.getBannerImage().isEmpty());
        Assertions.assertTrue(aProduct.getImages().isEmpty());
        Assertions.assertEquals(aCategoryId, aProduct.getCategoryId());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
        Assertions.assertNotNull(aProduct.getCreatedAt());
        Assertions.assertNotNull(aProduct.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aProduct.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testProductIdEqualsAndHashCode() {
        final var aProductId = ProductID.from("123456789");
        final var anotherProductId = ProductID.from("123456789");

        Assertions.assertTrue(aProductId.equals(anotherProductId));
        Assertions.assertTrue(aProductId.equals(aProductId));
        Assertions.assertFalse(aProductId.equals(null));
        Assertions.assertFalse(aProductId.equals(""));
        Assertions.assertEquals(aProductId.hashCode(), anotherProductId.hashCode());
    }

    @Test
    void givenAValidValues_whenCallWith_shouldBeInstantiateProduct() {
        final var aProductID = "123456789";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aBannerImage = ProductImage.with("abc", "banner-product.jpg", "/images/banner-product.jpg");
        final var aImage = ProductImage.with("abc", "product.jpg", "/images/product.jpg");
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aProduct = Product.with(
                aProductID,
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aBannerImage,
                Set.of(aImage),
                aCategoryId.getValue(),
                Set.of(aAttributes),
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertEquals(aProductID, aProduct.getId().getValue());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertEquals(aBannerImage.location(), aProduct.getBannerImage().get().location());
        Assertions.assertEquals(aImage.location(), aProduct.getImages().stream().findFirst().get().location());
        Assertions.assertEquals(aCategoryId.getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
        Assertions.assertEquals(aCreatedAt, aProduct.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aProduct.getUpdatedAt());
    }

    @Test
    void givenAValidProduct_whenCallWith_shouldBeInstantiateProduct() {
        final var aProduct = Fixture.Products.tshirt();

        final var aProductWith = Product.with(aProduct);

        Assertions.assertEquals(aProductWith.getId().getValue(), aProduct.getId().getValue());
        Assertions.assertEquals(aProductWith.getName(), aProduct.getName());
        Assertions.assertEquals(aProductWith.getDescription(), aProduct.getDescription());
        Assertions.assertEquals(aProductWith.getPrice(), aProduct.getPrice());
        Assertions.assertEquals(aProductWith.getQuantity(), aProduct.getQuantity());
        Assertions.assertTrue(aProductWith.getBannerImage().isEmpty());
        Assertions.assertTrue(aProductWith.getImages().isEmpty());
        Assertions.assertEquals(aProductWith.getCategoryId().getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertEquals(aProductWith.getAttributes(), aProduct.getAttributes());
        Assertions.assertEquals(aProductWith.getCreatedAt(), aProduct.getCreatedAt());
        Assertions.assertEquals(aProductWith.getUpdatedAt(), aProduct.getUpdatedAt());
    }

    @Test
    void givenAValidValuesWithNullAttributes_whenCallWith_shouldBeInstantiateProduct() {
        final var aProductID = "123456789";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aBannerImage = ProductImage.with("abc", "banner-product.jpg", "/images/banner-product.jpg");
        final var aImage = ProductImage.with("abc", "product.jpg", "/images/product.jpg");
        final var aCategoryId = CategoryID.from("1");
        final Set<ProductAttributes> aAttributes = null;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aProduct = Product.with(
                aProductID,
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aBannerImage,
                Set.of(aImage),
                aCategoryId.getValue(),
                aAttributes,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertEquals(aProductID, aProduct.getId().getValue());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertEquals(aBannerImage.location(), aProduct.getBannerImage().get().location());
        Assertions.assertEquals(aImage.id(), aProduct.getImages().stream().findFirst().get().id());
        Assertions.assertEquals(aCategoryId.getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertTrue(aProduct.getAttributes().isEmpty());
        Assertions.assertEquals(aCreatedAt, aProduct.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aProduct.getUpdatedAt());
    }

    @Test
    void givenAValidValuesWithNullImages_whenCallWith_shouldBeInstantiateProduct() {
        final var aProductID = "123456789";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = CategoryID.from("1");
        final ProductImage aBannerImage = null;
        final Set<ProductImage> aImages = null;
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aProduct = Product.with(
                aProductID,
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aBannerImage,
                aImages,
                aCategoryId.getValue(),
                Set.of(aAttributes),
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertEquals(aProductID, aProduct.getId().getValue());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aQuantity, aProduct.getQuantity());
        Assertions.assertTrue(aProduct.getBannerImage().isEmpty());
        Assertions.assertTrue(aProduct.getImages().isEmpty());
        Assertions.assertEquals(aCategoryId.getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertEquals(aAttributes.sku(), aProduct.getAttributes().stream().findFirst().get().sku());
        Assertions.assertEquals(aCreatedAt, aProduct.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aProduct.getUpdatedAt());
    }

    @Test
    void givenAValidImage_whenCallAddImage_shouldAddImage() {
        final var aProduct = Fixture.Products.tshirt();
        final var aImage = ProductImage.with("abc", "product.jpg", "/images/product.jpg");

        aProduct.addImage(aImage);

        Assertions.assertEquals(aImage, aProduct.getImages().stream().findFirst().get());
    }

    @Test
    void givenAnInvalidNullImage_whenCallAddImage_shouldNotAddImage() {
        final var aProduct = Fixture.Products.tshirt();
        final ProductImage aImage = null;

        Assertions.assertDoesNotThrow(() -> aProduct.addImage(aImage));
        Assertions.assertTrue(aProduct.getImages().isEmpty());
    }

    @Test
    void givenAValidImage_whenCallChangeBannerImage_shouldChangeBannerImage() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageId = IdUtils.generate().replace("-", "");
        final var aLocation = aProduct.getId().getValue() +
                "-" +
                aProductImageId +
                "-" +
                ProductImageType.BANNER.name() +
                "-" +
                "product.jpg";
        final var aUrl = "http://localhost:8080/images/product.jpg";
        final var aImage = ProductImage.with("abc", "product.jpg", aLocation, aUrl);

        aProduct.changeBannerImage(aImage);

        Assertions.assertEquals(aImage, aProduct.getBannerImage().get());
    }

    @Test
    void givenAValidType_whenCallProductImageTypeOf_shouldReturnProductImageType() {
        final var aProductImageType = ProductImageType.BANNER;

        final var aProductImageTypeOf = ProductImageType.of(aProductImageType.name());

        Assertions.assertEquals(aProductImageType, aProductImageTypeOf.get());
    }

    @Test
    void givenAnInvalidNullType_whenCallProductImageTypeOf_shouldReturnEmpty() {
        final String aProductImageType = null;

        final var aProductImageTypeOf = ProductImageType.of(aProductImageType);

        Assertions.assertTrue(aProductImageTypeOf.isEmpty());
    }

    @Test
    void givenAnInvalidProductImage_whenCallAddImageWithProductContains20Images_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aImage = ProductImage.with("abc", "product.jpg", "/images/product.jpg");

        for (int i = 0; i < 20; i++) {
            final var aOldImages = ProductImage.with(
                    "abc".concat("-").concat(String.valueOf(i)),
                    "product.jpg".concat("-").concat(String.valueOf(i)),
                    "/images/product.jpg".concat("-").concat(String.valueOf(i)));
            aProduct.addImage(aOldImages);
        }

        Assertions.assertThrows(
                DomainException.class,
                () -> aProduct.addImage(aImage),
                "Product can't have more than 20 images"
        );
    }

    @Test
    void givenAValidValues_whenCallUpdate_shouldUpdateProduct() {
        final var aProduct = Fixture.Products.tshirt();
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(50.95);
        final var aQuantity = 2;
        final var aCategoryId = CategoryID.from("1");

        final var aProductUpdatedAt = aProduct.getUpdatedAt();

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aQuantity, aCategoryId);

        Assertions.assertEquals(aName, aProductUpdated.getName());
        Assertions.assertEquals(aDescription, aProductUpdated.getDescription());
        Assertions.assertEquals(aPrice, aProductUpdated.getPrice());
        Assertions.assertEquals(aQuantity, aProductUpdated.getQuantity());
        Assertions.assertEquals(aCategoryId, aProductUpdated.getCategoryId());
        Assertions.assertTrue(aProductUpdatedAt.isBefore(aProductUpdated.getUpdatedAt()));
    }
}
