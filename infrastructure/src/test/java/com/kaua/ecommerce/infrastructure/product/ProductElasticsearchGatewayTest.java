package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.infrastructure.AbstractElasticsearchTest;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductElasticsearchGatewayTest extends AbstractElasticsearchTest {

    @Autowired
    private ProductElasticsearchGateway productElasticsearchGateway;

    @Autowired
    private ProductElasticsearchRepository productElasticsearchRepository;

    @Test
    void givenAValidProduct_whenCallSave_shouldReturnProductSaved() {
        final var aProduct = Fixture.Products.book();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        Assertions.assertEquals(0, this.productElasticsearchRepository.count());

        final var actualProduct = this.productElasticsearchGateway.save(aProduct);

        Assertions.assertEquals(1, this.productElasticsearchRepository.count());

        Assertions.assertEquals(aProduct.getId().getValue(), actualProduct.getId().getValue());
        Assertions.assertEquals(aProduct.getName(), actualProduct.getName());
        Assertions.assertEquals(aProduct.getDescription(), actualProduct.getDescription());
        Assertions.assertEquals(aProduct.getPrice(), actualProduct.getPrice());
        Assertions.assertEquals(aProduct.getQuantity(), actualProduct.getQuantity());
        Assertions.assertEquals(aProduct.getBannerImage().get().getLocation(), actualProduct.getBannerImage().get().getLocation());
        Assertions.assertEquals(aProduct.getImages().size(), actualProduct.getImages().size());
        Assertions.assertEquals(aProduct.getCategoryId().getValue(), actualProduct.getCategoryId().getValue());
        Assertions.assertEquals(aProduct.getAttributes().size(), actualProduct.getAttributes().size());
        Assertions.assertEquals(aProduct.getStatus(), actualProduct.getStatus());
        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), actualProduct.getUpdatedAt());
    }

    @Test
    void givenAValidProduct_whenCallDeleteById_shouldReturnProductSaved() {
        final var aProduct = Fixture.Products.book();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));
        this.productElasticsearchRepository.save(ProductElasticsearchEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productElasticsearchRepository.count());

        Assertions.assertDoesNotThrow(() -> this.productElasticsearchGateway
                .deleteById(aProduct.getId().getValue()));

        Assertions.assertEquals(0, this.productElasticsearchRepository.count());
    }

    @Test
    void testNotImplementedMethods() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> productElasticsearchGateway.findAll(null));

        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> productElasticsearchGateway.findById(null));

        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> productElasticsearchGateway.findByIdNested(null));
    }
}
