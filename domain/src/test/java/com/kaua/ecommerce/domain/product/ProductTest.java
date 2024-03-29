package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.events.ProductCreatedEvent;
import com.kaua.ecommerce.domain.product.events.ProductDeletedEvent;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class ProductTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewProduct_shouldBeInstantiateProduct() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        Assertions.assertNotNull(aProduct.getId());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertTrue(aProduct.getBannerImage().isEmpty());
        Assertions.assertTrue(aProduct.getImages().isEmpty());
        Assertions.assertEquals(aCategoryId, aProduct.getCategoryId());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
        Assertions.assertEquals(ProductStatus.ACTIVE, aProduct.getStatus());
        Assertions.assertNotNull(aProduct.getCreatedAt());
        Assertions.assertNotNull(aProduct.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aProduct.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesWithoutDescription_whenCallNewProduct_shouldBeInstantiateProduct() {
        final var aName = "Product Name";
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aProduct = Product.newProduct(aName, aDescription, aPrice, aCategoryId, Set.of(aAttributes));

        Assertions.assertNotNull(aProduct.getId());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertNull(aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertTrue(aProduct.getBannerImage().isEmpty());
        Assertions.assertTrue(aProduct.getImages().isEmpty());
        Assertions.assertEquals(aCategoryId, aProduct.getCategoryId());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
        Assertions.assertEquals(ProductStatus.ACTIVE, aProduct.getStatus());
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
        final var aBannerImage = ProductImage.with("abc", "banner-product.jpg", "/images/banner-product.jpg");
        final var aImage = ProductImage.with("abc", "product.jpg", "/images/product.jpg");
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );
        final var aStatus = ProductStatus.INACTIVE;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();
        final var aVersion = 0;

        final var aProduct = Product.with(
                aProductID,
                aName,
                aDescription,
                aPrice,
                aBannerImage,
                Set.of(aImage),
                aCategoryId.getValue(),
                Set.of(aAttributes),
                aStatus,
                aCreatedAt,
                aUpdatedAt,
                aVersion
        );

        Assertions.assertEquals(aProductID, aProduct.getId().getValue());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aBannerImage.getLocation(), aProduct.getBannerImage().get().getLocation());
        Assertions.assertEquals(aImage.getLocation(), aProduct.getImages().stream().findFirst().get().getLocation());
        Assertions.assertEquals(aCategoryId.getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertEquals(aAttributes, aProduct.getAttributes().stream().findFirst().get());
        Assertions.assertEquals(aStatus, aProduct.getStatus());
        Assertions.assertEquals(aCreatedAt, aProduct.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aProduct.getUpdatedAt());
        Assertions.assertEquals(aVersion, aProduct.getVersion());
    }

    @Test
    void givenAValidProduct_whenCallWith_shouldBeInstantiateProduct() {
        final var aProduct = Fixture.Products.tshirt();

        final var aProductWith = Product.with(aProduct);

        Assertions.assertEquals(aProductWith.getId().getValue(), aProduct.getId().getValue());
        Assertions.assertEquals(aProductWith.getName(), aProduct.getName());
        Assertions.assertEquals(aProductWith.getDescription(), aProduct.getDescription());
        Assertions.assertEquals(aProductWith.getPrice(), aProduct.getPrice());
        Assertions.assertTrue(aProductWith.getBannerImage().isEmpty());
        Assertions.assertTrue(aProductWith.getImages().isEmpty());
        Assertions.assertEquals(aProductWith.getCategoryId().getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertEquals(aProductWith.getAttributes(), aProduct.getAttributes());
        Assertions.assertEquals(aProductWith.getStatus(), aProduct.getStatus());
        Assertions.assertEquals(aProductWith.getCreatedAt(), aProduct.getCreatedAt());
        Assertions.assertEquals(aProductWith.getUpdatedAt(), aProduct.getUpdatedAt());
        Assertions.assertEquals(aProductWith.getVersion(), aProduct.getVersion());
    }

    @Test
    void givenAValidValuesWithNullAttributes_whenCallWith_shouldBeInstantiateProduct() {
        final var aProductID = "123456789";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aBannerImage = ProductImage.with("abc", "banner-product.jpg", "/images/banner-product.jpg");
        final var aImage = ProductImage.with("abc", "product.jpg", "/images/product.jpg");
        final var aCategoryId = CategoryID.from("1");
        final Set<ProductAttributes> aAttributes = null;
        final var aStatus = ProductStatus.INACTIVE;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();
        final var aVersion = 0;

        final var aProduct = Product.with(
                aProductID,
                aName,
                aDescription,
                aPrice,
                aBannerImage,
                Set.of(aImage),
                aCategoryId.getValue(),
                aAttributes,
                aStatus,
                aCreatedAt,
                aUpdatedAt,
                aVersion
        );

        Assertions.assertEquals(aProductID, aProduct.getId().getValue());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertEquals(aBannerImage.getLocation(), aProduct.getBannerImage().get().getLocation());
        Assertions.assertEquals(aImage.getId(), aProduct.getImages().stream().findFirst().get().getId());
        Assertions.assertEquals(aCategoryId.getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertTrue(aProduct.getAttributes().isEmpty());
        Assertions.assertEquals(aStatus, aProduct.getStatus());
        Assertions.assertEquals(aCreatedAt, aProduct.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aProduct.getUpdatedAt());
        Assertions.assertEquals(aVersion, aProduct.getVersion());
    }

    @Test
    void givenAValidValuesWithNullImages_whenCallWith_shouldBeInstantiateProduct() {
        final var aProductID = "123456789";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final ProductImage aBannerImage = null;
        final Set<ProductImage> aImages = null;
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );
        final var aStatus = ProductStatus.INACTIVE;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();
        final var aVersion = 0;

        final var aProduct = Product.with(
                aProductID,
                aName,
                aDescription,
                aPrice,
                aBannerImage,
                aImages,
                aCategoryId.getValue(),
                Set.of(aAttributes),
                aStatus,
                aCreatedAt,
                aUpdatedAt,
                aVersion
        );

        Assertions.assertEquals(aProductID, aProduct.getId().getValue());
        Assertions.assertEquals(aName, aProduct.getName());
        Assertions.assertEquals(aDescription, aProduct.getDescription());
        Assertions.assertEquals(aPrice, aProduct.getPrice());
        Assertions.assertTrue(aProduct.getBannerImage().isEmpty());
        Assertions.assertTrue(aProduct.getImages().isEmpty());
        Assertions.assertEquals(aCategoryId.getValue(), aProduct.getCategoryId().getValue());
        Assertions.assertEquals(aAttributes.getSku(), aProduct.getAttributes().stream().findFirst().get().getSku());
        Assertions.assertEquals(aStatus, aProduct.getStatus());
        Assertions.assertEquals(aCreatedAt, aProduct.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aProduct.getUpdatedAt());
        Assertions.assertEquals(aVersion, aProduct.getVersion());
    }

    @Test
    void givenAValidImage_whenCallAddImage_shouldAddImage() {
        final var aProduct = Fixture.Products.tshirt();
        final var aImage = ProductImage.with("abc", "product.jpg", "/images/product.jpg");

        final var aProductUpdatedAtBefore = aProduct.getUpdatedAt();

        aProduct.addImage(aImage);

        final var aProductUpdatedAtAfter = aProduct.getUpdatedAt();

        Assertions.assertEquals(aImage, aProduct.getImages().stream().findFirst().get());
        Assertions.assertTrue(aProductUpdatedAtBefore.isBefore(aProductUpdatedAtAfter));
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

        final var aProductUpdatedAtBefore = aProduct.getUpdatedAt();

        aProduct.changeBannerImage(aImage);

        final var aProductUpdatedAtAfter = aProduct.getUpdatedAt();

        Assertions.assertEquals(aImage, aProduct.getBannerImage().get());
        Assertions.assertTrue(aProductUpdatedAtBefore.isBefore(aProductUpdatedAtAfter));
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
        final var aCategoryId = CategoryID.from("1");

        final var aProductUpdatedAt = aProduct.getUpdatedAt();

        final var aProductUpdated = aProduct.update(aName, aDescription, aPrice, aCategoryId);

        Assertions.assertEquals(aName, aProductUpdated.getName());
        Assertions.assertEquals(aDescription, aProductUpdated.getDescription());
        Assertions.assertEquals(aPrice, aProductUpdated.getPrice());
        Assertions.assertEquals(aCategoryId, aProductUpdated.getCategoryId());
        Assertions.assertTrue(aProductUpdatedAt.isBefore(aProductUpdated.getUpdatedAt()));
    }

    @Test
    void givenAValidValues_whenCallUpdateStatus_shouldUpdateProductStatus() {
        final var aProduct = Fixture.Products.tshirt();
        final var aStatus = ProductStatus.INACTIVE;

        final var aProductUpdatedAt = aProduct.getUpdatedAt();

        final var aProductUpdated = aProduct.updateStatus(aStatus);

        Assertions.assertEquals(aStatus, aProductUpdated.getStatus());
        Assertions.assertTrue(aProductUpdatedAt.isBefore(aProductUpdated.getUpdatedAt()));
    }

    @Test
    void givenAValidStatus_whenCallProductStatusOf_shouldReturnProductStatus() {
        final var aProductStatus = ProductStatus.INACTIVE;

        final var aProductStatusOf = ProductStatus.of(aProductStatus.name());

        Assertions.assertEquals(aProductStatus, aProductStatusOf.get());
    }

    @Test
    void givenAnInvalidNullStatus_whenCallProductStatusOf_shouldReturnEmpty() {
        final String aProductStatus = null;

        final var aProductStatusOf = ProductStatus.of(aProductStatus);

        Assertions.assertTrue(aProductStatusOf.isEmpty());
    }

    @Test
    void givenAValidProduct_whenCallRegisterEvent_shouldReturnProductWithDomainEvent() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aDomainEventType = EventsTypes.PRODUCT_CREATED;

        final var aProduct = Product.newProduct(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                Set.of(aAttributes));

        final var aProductCreatedEvent = ProductCreatedEvent.from(aProduct);
        aProduct.registerEvent(aProductCreatedEvent);

        Assertions.assertEquals(aDomainEventType, aProduct.getDomainEvents().stream().findFirst().get().eventType());
        Assertions.assertEquals(1, aProduct.getDomainEvents().size());
    }

    @Test
    void givenAValidProduct_whenCallProductUpdatedEventFrom_shouldReturnProductUpdatedEvent() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aProduct = Product.newProduct(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                Set.of(aAttributes));
        final var aProductUpdatedStatus = aProduct.updateStatus(ProductStatus.INACTIVE);

        final var aProductUpdated = ProductUpdatedEvent.from(aProductUpdatedStatus);

        Assertions.assertNotNull(aProductUpdated);
        Assertions.assertEquals(aProduct.getId().getValue(), aProductUpdated.id());
    }

    @Test
    void givenAValidProduct_whenCallProductDeletedEventFrom_shouldReturnProductDeletedEvent() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");
        final var aAttributes = ProductAttributes.with(
                ProductColor.with("1", "RED"),
                ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                aName
        );

        final var aProduct = Product.newProduct(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                Set.of(aAttributes));
        final var aProductUpdatedStatus = aProduct.updateStatus(ProductStatus.DELETED);

        final var aProductDeleted = ProductDeletedEvent.from(aProductUpdatedStatus);

        Assertions.assertNotNull(aProductDeleted);
        Assertions.assertEquals(aProduct.getId().getValue(), aProductDeleted.id());
    }

    @Test
    void givenAValidProduct_whenCallToString_shouldReturnProductToString() {
        final var aProduct = Fixture.Products.tshirt();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        final var aProductToString = aProduct.toString();

        Assertions.assertNotNull(aProductToString);
    }

    @Test
    void givenAValidLocation_whenCallRemoveImage_shouldRemoveImage() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImage = Fixture.Products.productImage(ProductImageType.GALLERY);
        aProduct.addImage(aProductImage);

        final var aProductUpdatedAtBefore = aProduct.getUpdatedAt();

        final var aResult = aProduct.removeImage(aProductImage.getLocation());

        final var aProductUpdatedAtAfter = aProduct.getUpdatedAt();

        Assertions.assertTrue(aProduct.getImages().isEmpty());
        Assertions.assertEquals(aProductImage, aResult);
        Assertions.assertTrue(aProductUpdatedAtBefore.isBefore(aProductUpdatedAtAfter));
    }

    @ParameterizedTest
    @CsvSource({
            "null",
            "empty",
            "another-location"
    })
    void givenAnInvalidLocation_whenCallRemoveImage_shouldReturnNull(final String aLocation) {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImage = Fixture.Products.productImage(ProductImageType.GALLERY);
        aProduct.addImage(aProductImage);

        final var aLocationToRemove = aLocation.contains("null")
                ? null
                : aLocation.contains("empty")
                ? ""
                : aLocation;

        final var aProductUpdatedAtBefore = aProduct.getUpdatedAt();

        final var aResult = aProduct.removeImage(aLocationToRemove);

        final var aProductUpdatedAtAfter = aProduct.getUpdatedAt();

        Assertions.assertFalse(aProduct.getImages().isEmpty());
        Assertions.assertNull(aResult);
        Assertions.assertEquals(aProductUpdatedAtBefore, aProductUpdatedAtAfter);
    }

    @Test
    void givenAValidAttribute_whenCallAddAttribute_shouldAddAttribute() {
        final var aProduct = Fixture.Products.tshirt();
        final var aAttribute = Fixture.Products.productAttributes(aProduct.getName());

        final var aProductUpdatedAtBefore = aProduct.getUpdatedAt();

        aProduct.addAttribute(aAttribute);

        final var aProductUpdatedAtAfter = aProduct.getUpdatedAt();

        Assertions.assertEquals(2, aProduct.getAttributes().size());
        Assertions.assertTrue(aProductUpdatedAtBefore.isBefore(aProductUpdatedAtAfter));
    }

    @Test
    void givenAnInvalidNullAttribute_whenCallAddAttribute_shouldNotAddAttribute() {
        final var aProduct = Fixture.Products.tshirt();
        final ProductAttributes aAttribute = null;

        Assertions.assertDoesNotThrow(() -> aProduct.addAttribute(aAttribute));
        Assertions.assertEquals(1, aProduct.getAttributes().size());
    }

    @Test
    void givenAnInvalidProductAttributes_whenCallAddAttributeWithProductContains10Attributes_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aAttributes = ProductAttributes.create(
                ProductColor.with("Gray"),
                ProductSize.with("M", 0.5, 0.5, 0.5, 0.5),
                aProduct.getName()
        );

        for (int i = 0; i < 9; i++) {
            final var aOldAttributes = ProductAttributes.with(
                    ProductColor.with("Color".concat(String.valueOf(i))),
                    ProductSize.with("Size".concat(String.valueOf(i)), 0.5, 0.5, 0.5, 0.5),
                    aProduct.getName()
            );
            aProduct.addAttribute(aOldAttributes);
        }

        final var aException = Assertions.assertThrows(
                DomainException.class,
                () -> aProduct.addAttribute(aAttributes)
        );

        Assertions.assertEquals("Product can't have more than 10 attributes", aException.getMessage());
    }

    @Test
    void givenAnInvalid11ProductAttributes_whenCallNewProduct_shouldThrowDomainException() {
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = CategoryID.from("1");

        final var aProductAttributes = new HashSet<ProductAttributes>();
        for (int i = 0; i < 11; i++) {
            final var aOldAttributes = ProductAttributes.create(
                    ProductColor.with("Color".concat(String.valueOf(i))),
                    ProductSize.with("Size".concat(String.valueOf(i)), 0.5, 0.5, 0.5, 0.5),
                    "Product Name"
            );
            aProductAttributes.add(aOldAttributes);
        }

        final var aException = Assertions.assertThrows(
                DomainException.class,
                () -> Product.newProduct(
                        "Product Name",
                        "Product Description",
                        aPrice,
                        aCategoryId,
                        aProductAttributes
                )
        );

        Assertions.assertEquals("Product can't have more than 10 attributes", aException.getMessage());
    }

    @Test
    void givenAValidSku_whenCallRemoveAttribute_shouldRemoveAttribute() {
        final var aProduct = Fixture.Products.tshirt();
        final var aAttribute = Fixture.Products.productAttributes(aProduct.getName());
        aProduct.addAttribute(aAttribute);

        final var aProductUpdatedAtBefore = aProduct.getUpdatedAt();

        Assertions.assertDoesNotThrow(() -> aProduct.removeAttribute(aAttribute.getSku()));

        final var aProductUpdatedAtAfter = aProduct.getUpdatedAt();

        Assertions.assertEquals(1, aProduct.getAttributes().size());
        Assertions.assertTrue(aProductUpdatedAtBefore.isBefore(aProductUpdatedAtAfter));
    }

    @ParameterizedTest
    @CsvSource({
            "null",
            "empty",
            "another-sku"
    })
    void givenAnInvalidSku_whenCallRemoveAttribute_shouldReturnNull(final String aSku) {
        final var aProduct = Fixture.Products.tshirt();

        final var aSkuToRemove = aSku.contains("null")
                ? null
                : aSku.contains("empty")
                ? ""
                : aSku;

        final var aProductUpdatedAtBefore = aProduct.getUpdatedAt();

        final var aResult = aProduct.removeAttribute(aSkuToRemove);

        final var aProductUpdatedAtAfter = aProduct.getUpdatedAt();

        Assertions.assertEquals(1, aProduct.getAttributes().size());
        Assertions.assertNull(aResult);
        Assertions.assertEquals(aProductUpdatedAtBefore, aProductUpdatedAtAfter);
    }
}
