package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.service.StorageService;
import com.kaua.ecommerce.infrastructure.service.local.InMemoryStorageServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@IntegrationTest
public class ProductMediaResourceGatewayTest {

    @Autowired
    private MediaResourceGateway mediaResourceGateway;

    @Autowired
    private StorageService storageService;

    @BeforeEach
    public void setup() {
        storageService().clear();
    }

    @Test
    void testInjection() {
        Assertions.assertNotNull(mediaResourceGateway);
        Assertions.assertInstanceOf(ProductMediaResourceGateway.class, mediaResourceGateway);

        Assertions.assertNotNull(storageService);
        Assertions.assertInstanceOf(InMemoryStorageServiceImpl.class, storageService);
    }

    @Test
    void givenValidResource_whenCallsStorageImage_shouldStoreIt() {
        // given
        final var aProductId = ProductID.unique();
        final var aResource = Fixture.Products.productImageResource(ProductImageType.BANNER);

        // when
        final var actualMedia =
                this.mediaResourceGateway.storeImage(aProductId, aResource);

        // then
        Assertions.assertNotNull(actualMedia.getId());
        Assertions.assertNotNull(actualMedia.getLocation());
        Assertions.assertEquals(aResource.resource().fileName(), actualMedia.getName());
        Assertions.assertNotNull(actualMedia.getUrl());

        Assertions.assertTrue(storageService().storage().containsKey(actualMedia.getLocation()));
        Assertions.assertEquals(1, storageService().storage().size());
    }

    @Test
    void givenValidResource_whenCallsClearImage_shouldDeleteImage() {
        // given
        final var aProductId = ProductID.unique();
        final var aResource = Fixture.Products.productImageResource(ProductImageType.BANNER);

        final var aProductImage = this.mediaResourceGateway.storeImage(aProductId, aResource);

        // when
        Assertions.assertDoesNotThrow(() -> this.mediaResourceGateway.clearImage(aProductImage));

        // then
        Assertions.assertEquals(0, storageService().storage().size());
    }

    @Test
    void givenValidResource_whenCallsClearImages_shouldDeleteImages() {
        // given
        final var aProductId = ProductID.unique();
        final var aResourceOne = Fixture.Products.productImageResource(ProductImageType.BANNER);
        final var aResourceTwo = Fixture.Products.productImageResource(ProductImageType.GALLERY);

        final var aProductImageOne = this.mediaResourceGateway.storeImage(aProductId, aResourceOne);
        final var aProductImageTwo = this.mediaResourceGateway.storeImage(aProductId, aResourceTwo);

        final var aProductImages = Set.of(aProductImageOne, aProductImageTwo);

        // when
        Assertions.assertEquals(2, storageService().storage().size());

        Assertions.assertDoesNotThrow(() -> this.mediaResourceGateway.clearImages(aProductImages));

        // then
        Assertions.assertEquals(0, storageService().storage().size());
    }

    private InMemoryStorageServiceImpl storageService() {
        return (InMemoryStorageServiceImpl) storageService;
    }
}
