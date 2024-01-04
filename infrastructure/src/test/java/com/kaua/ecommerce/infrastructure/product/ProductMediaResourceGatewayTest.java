package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.service.StorageService;
import com.kaua.ecommerce.infrastructure.service.local.InMemoryStorageServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        final var aResource = Fixture.Products.imageCoverTypeResource();

        // when
        final var actualMedia =
                this.mediaResourceGateway.storeImage(aProductId, aResource);

        // then
        Assertions.assertNotNull(actualMedia.id());
        Assertions.assertNotNull(actualMedia.location());
        Assertions.assertEquals(aResource.resource().fileName(), actualMedia.name());
        Assertions.assertNotNull(actualMedia.url());

        Assertions.assertTrue(storageService().storage().containsKey(actualMedia.location()));
        Assertions.assertEquals(1, storageService().storage().size());
    }

    @Test
    void givenValidResource_whenCallsClearImage_shouldDeleteImage() {
        // given
        final var aProductId = ProductID.unique();
        final var aResource = Fixture.Products.imageCoverTypeResource();

        final var aProductImage = this.mediaResourceGateway.storeImage(aProductId, aResource);

        // when
        Assertions.assertDoesNotThrow(() -> this.mediaResourceGateway.clearImage(aProductImage));

        // then
        Assertions.assertEquals(0, storageService().storage().size());
    }

    private InMemoryStorageServiceImpl storageService() {
        return (InMemoryStorageServiceImpl) storageService;
    }
}
