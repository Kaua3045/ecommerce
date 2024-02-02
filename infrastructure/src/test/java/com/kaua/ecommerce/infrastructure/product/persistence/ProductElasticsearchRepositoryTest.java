package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.product.*;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.AbstractElasticsearchTest;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

public class ProductElasticsearchRepositoryTest extends AbstractElasticsearchTest {

    @Autowired
    private ProductElasticsearchRepository productElasticsearchRepository;

    @Test
    void givenAValidProduct_whenCallSave_shouldPersistProduct() {
        final var aProduct = Fixture.Products.book();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        Assertions.assertEquals(0, this.productElasticsearchRepository.count());

        final var actualProduct = this.productElasticsearchRepository
                .save(ProductElasticsearchEntity.toEntity(aProduct)).toDomain();

        Assertions.assertEquals(1, this.productElasticsearchRepository.count());

        final var aPersistedProduct = this.productElasticsearchRepository.findById(aProduct.getId().getValue()).get();

        Assertions.assertEquals(actualProduct.getId().getValue(), aPersistedProduct.getId());
        Assertions.assertEquals(actualProduct.getName(), aPersistedProduct.getName());
        Assertions.assertEquals(actualProduct.getDescription(), aPersistedProduct.getDescription());
        Assertions.assertEquals(actualProduct.getPrice(), aPersistedProduct.getPrice());
        Assertions.assertEquals(actualProduct.getQuantity(), aPersistedProduct.getQuantity());
        Assertions.assertEquals(actualProduct.getBannerImage().get().getId(), aPersistedProduct.getBannerImage().get().getId());
        Assertions.assertEquals(actualProduct.getBannerImage().get().getName(), aPersistedProduct.getBannerImage().get().getName());
        Assertions.assertEquals(actualProduct.getBannerImage().get().getLocation(), aPersistedProduct.getBannerImage().get().getLocation());
        Assertions.assertEquals(actualProduct.getBannerImage().get().getUrl(), aPersistedProduct.getBannerImage().get().getUrl());
        Assertions.assertEquals(actualProduct.getImages().size(), aPersistedProduct.getImages().size());
        Assertions.assertEquals(actualProduct.getCategoryId().getValue(), aPersistedProduct.getCategoryId());
        Assertions.assertEquals(actualProduct.getAttributes().stream().findFirst().get()
                .getSize().getId(), aPersistedProduct.getAttributes().stream().findFirst().get().getSize().getId());
        Assertions.assertEquals(actualProduct.getAttributes().stream().findFirst().get()
                .getColor().getId(), aPersistedProduct.getAttributes().stream().findFirst().get().getColor().getId());
        Assertions.assertEquals(actualProduct.getAttributes().stream().findFirst().get()
                .getSku(), aPersistedProduct.getAttributes().stream().findFirst().get().getSku());
        Assertions.assertEquals(actualProduct.getStatus(), aPersistedProduct.getStatus());
        Assertions.assertNotNull(aPersistedProduct.getCreatedAt());
        Assertions.assertNotNull(aPersistedProduct.getUpdatedAt());
    }

    @Test
    void givenAValidProductAndSetOtherValues_whenCallSave_shouldPersistProduct() {
        final var aProduct = Fixture.Products.book();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        final var aProductImage = Fixture.Products.productImage(ProductImageType.GALLERY);
        final var aProductImageEntity = ProductImageElasticsearchEntity.toEntity(aProductImage);
        aProductImageEntity.setId(IdUtils.generateWithoutDash());
        aProductImageEntity.setName("name.png");
        aProductImageEntity.setLocation("location");
        aProductImageEntity.setUrl("url");

        final var aProductColor = ProductColor.with("Red");
        final var aProductColorEntity = ProductColorElasticsearchEntity.toEntity(aProductColor);
        aProductColorEntity.setId(IdUtils.generate());
        aProductColorEntity.setColor("Blue");
        final var aProductColorAfterSet = aProductColorEntity.toDomain();

        final var aProductSize = ProductSize.with("M", 5, 0.5, 0.5, 0.5);
        final var aProductSizeEntity = ProductSizeElasticsearchEntity.toEntity(aProductSize);
        aProductSizeEntity.setId(IdUtils.generate());
        aProductSizeEntity.setSize("G");
        aProductSizeEntity.setWeight(10.0);
        aProductSizeEntity.setHeight(1.0);
        aProductSizeEntity.setWidth(1.0);
        aProductSizeEntity.setDepth(1.0);
        final var aProductSizeAfterSet = aProductSizeEntity.toDomain();

        final var aEntity = ProductElasticsearchEntity.toEntity(aProduct);
        aEntity.setId(ProductID.unique().getValue());
        aEntity.setName("Other Name");
        aEntity.setDescription("Other Description");
        aEntity.setPrice(BigDecimal.valueOf(1000.0));
        aEntity.setQuantity(10);
        aEntity.setBannerImage(null);
        aEntity.setCategoryId(CategoryID.unique().getValue());
        aEntity.setStatus(ProductStatus.INACTIVE);
        aEntity.setCreatedAt(InstantUtils.now());
        aEntity.setUpdatedAt(InstantUtils.now());
        aEntity.setImages(Set.of(aProductImageEntity));
        aEntity.setAttributes(Set.of(ProductAttributesElasticsearchEntity.toEntity(
                ProductAttributes.with(aProductColorAfterSet, aProductSizeAfterSet, "sku"))));

        Assertions.assertEquals(0, this.productElasticsearchRepository.count());

        final var actualProduct = this.productElasticsearchRepository
                .save(aEntity).toDomain();

        Assertions.assertEquals(1, this.productElasticsearchRepository.count());

        final var aPersistedProduct = this.productElasticsearchRepository.findById(actualProduct.getId().getValue()).get();

        Assertions.assertEquals(actualProduct.getId().getValue(), aPersistedProduct.getId());
        Assertions.assertEquals(actualProduct.getName(), aPersistedProduct.getName());
        Assertions.assertEquals(actualProduct.getDescription(), aPersistedProduct.getDescription());
        Assertions.assertEquals(actualProduct.getPrice(), aPersistedProduct.getPrice());
        Assertions.assertEquals(actualProduct.getQuantity(), aPersistedProduct.getQuantity());
        Assertions.assertTrue(actualProduct.getBannerImage().isEmpty());
        Assertions.assertEquals(actualProduct.getImages().size(), aPersistedProduct.getImages().size());
        Assertions.assertEquals(actualProduct.getCategoryId().getValue(), aPersistedProduct.getCategoryId());
        Assertions.assertEquals(1, actualProduct.getAttributes().size());
        Assertions.assertEquals(actualProduct.getStatus(), aPersistedProduct.getStatus());
        Assertions.assertNotNull(aPersistedProduct.getCreatedAt());
        Assertions.assertNotNull(aPersistedProduct.getUpdatedAt());
    }
}
