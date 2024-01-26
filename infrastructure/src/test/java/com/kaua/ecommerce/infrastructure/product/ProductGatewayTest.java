package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.product.*;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

@DatabaseGatewayTest
public class ProductGatewayTest {

    @Autowired
    private ProductMySQLGateway productGateway;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private ProductColorJpaRepository productColorRepository;

    @Test
    void givenAValidProduct_whenCallCreate_shouldPersistProduct() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = CategoryID.unique();
        final var aColor = "RED";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var aProduct = Product.newProduct(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                Set.of(ProductAttributes.create(
                        ProductColor.with(aColor),
                        ProductSize.with(aSize, aWeight, aHeight, aWidth, aDepth),
                        aName
                ))
        );

        Assertions.assertEquals(0, this.productRepository.count());

        final var createdProduct = this.productGateway.create(aProduct);

        Assertions.assertEquals(1, this.productRepository.count());

        final var aPersistedProduct = this.productRepository.findById(createdProduct.getId().getValue()).get();

        Assertions.assertEquals(aProduct.getId().getValue(), aPersistedProduct.getId());
        Assertions.assertEquals(aProduct.getName(), aPersistedProduct.getName());
        Assertions.assertEquals(aProduct.getDescription(), aPersistedProduct.getDescription());
        Assertions.assertEquals(aProduct.getPrice(), aPersistedProduct.getPrice());
        Assertions.assertEquals(aProduct.getQuantity(), aPersistedProduct.getQuantity());
        Assertions.assertEquals(aProduct.getCategoryId().getValue(), aPersistedProduct.getCategoryId());
        Assertions.assertEquals(0, aPersistedProduct.getImages().size());
        Assertions.assertNotNull(aPersistedProduct.getAttributes().stream().findFirst().get().getId());
        Assertions.assertNotNull(aPersistedProduct.getAttributes().stream().findFirst().get().getProduct());
        Assertions.assertEquals(aColor, aPersistedProduct.getAttributes().stream().findFirst().get().getColor().getColor());
        Assertions.assertEquals(aSize, aPersistedProduct.getAttributes().stream().findFirst().get().getSize().getSize());
        Assertions.assertEquals(aProduct.getCreatedAt(), aPersistedProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), aPersistedProduct.getUpdatedAt());
    }

    @Test
    void givenAValidProductId_whenCallFindById_shouldReturnProduct() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImage = Fixture.Products.imageBannerType();
        aProduct.addImage(aProductImage);
        this.productRepository.save(ProductJpaEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productRepository.count());

        final var aPersistedProduct = this.productGateway.findById(aProduct.getId().getValue()).get();

        Assertions.assertEquals(aProduct.getId().getValue(), aPersistedProduct.getId().getValue());
        Assertions.assertEquals(aProduct.getName(), aPersistedProduct.getName());
        Assertions.assertEquals(aProduct.getDescription(), aPersistedProduct.getDescription());
        Assertions.assertEquals(aProduct.getPrice(), aPersistedProduct.getPrice());
        Assertions.assertEquals(aProduct.getQuantity(), aPersistedProduct.getQuantity());
        Assertions.assertEquals(aProduct.getCategoryId().getValue(), aPersistedProduct.getCategoryId().getValue());
        Assertions.assertEquals(1, aPersistedProduct.getImages().size());
        Assertions.assertEquals(aProduct.getAttributes().stream().findFirst().get().sku(), aPersistedProduct.getAttributes().stream().findFirst().get().sku());
        Assertions.assertEquals(aProduct.getAttributes().stream().findFirst().get().color().color(), aPersistedProduct.getAttributes().stream().findFirst().get().color().color());
        Assertions.assertEquals(aProduct.getAttributes().stream().findFirst().get().size().size(), aPersistedProduct.getAttributes().stream().findFirst().get().size().size());
        Assertions.assertEquals(aProduct.getCreatedAt(), aPersistedProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), aPersistedProduct.getUpdatedAt());
    }

    @Test
    void givenAnInvalidProductId_whenCallFindById_shouldReturnEmpty() {
        final var aProductId = ProductID.unique();

        Assertions.assertEquals(0, this.productRepository.count());

        final var aProduct = this.productGateway.findById(aProductId.getValue());

        Assertions.assertTrue(aProduct.isEmpty());
    }

    @Test
    void givenAValidColorName_whenCallFindColorByName_shouldReturnProductColor() {
        final var aProductColor = ProductColor.with("Red");
        this.productColorRepository.save(ProductColorJpaEntity.toEntity(aProductColor));

        Assertions.assertEquals(1, this.productColorRepository.count());

        final var aPersistedProductColor = this.productGateway.findColorByName(aProductColor.color()).get();

        Assertions.assertEquals(aProductColor.id(), aPersistedProductColor.id());
        Assertions.assertEquals(aProductColor.color(), aPersistedProductColor.color());
    }

    @Test
    void givenAnInvalidColorName_whenCallFindColorByName_shouldReturnEmpty() {
        final var aProductColorName = "blue";

        Assertions.assertEquals(0, this.productColorRepository.count());

        final var aProduct = this.productGateway.findColorByName(aProductColorName);

        Assertions.assertTrue(aProduct.isEmpty());
    }

    @Test
    void givenAValidProductWithImages_whenCallUpdate_shouldUpdateProduct() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImage = Fixture.Products.imageBannerType();

        this.productRepository.save(ProductJpaEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productRepository.count());

        aProduct.addImage(aProductImage);

        final var updatedProduct = this.productGateway.update(aProduct);

        Assertions.assertEquals(1, this.productRepository.count());

        final var aPersistedProduct = this.productRepository.findById(updatedProduct.getId().getValue()).get();

        Assertions.assertEquals(aProduct.getId().getValue(), aPersistedProduct.getId());
        Assertions.assertEquals(aProduct.getName(), aPersistedProduct.getName());
        Assertions.assertEquals(aProduct.getDescription(), aPersistedProduct.getDescription());
        Assertions.assertEquals(aProduct.getPrice(), aPersistedProduct.getPrice());
        Assertions.assertEquals(aProduct.getQuantity(), aPersistedProduct.getQuantity());
        Assertions.assertEquals(aProduct.getCategoryId().getValue(), aPersistedProduct.getCategoryId());
        Assertions.assertEquals(1, aPersistedProduct.getImages().size());
        Assertions.assertNotNull(aPersistedProduct.getAttributes().stream().findFirst().get().getSku());
        Assertions.assertEquals(aProduct.getCreatedAt(), aPersistedProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), aPersistedProduct.getUpdatedAt());
    }
}
