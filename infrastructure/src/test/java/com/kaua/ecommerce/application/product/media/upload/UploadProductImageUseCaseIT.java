package com.kaua.ecommerce.application.product.media.upload;

import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageCommand;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class UploadProductImageUseCaseIT {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private UploadProductImageUseCase uploadProductImageUseCase;

    @Test
    void givenAValidValuesWithCoverType_whenCallsUploadProductImage_shouldUploadImage() {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aProductId = aProduct.getId().getValue();

        this.productJpaRepository.save(ProductJpaEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productJpaRepository.count());

        final var aCommand = UploadProductImageCommand.with(
                aProductId,
                Fixture.Products.imageCoverTypeResource()
        );

        // when
        final var actualResult = this.uploadProductImageUseCase.execute(aCommand);

        // then
        Assertions.assertEquals(aProductId, actualResult.productId());
        Assertions.assertEquals(ProductImageType.COVER.name(), actualResult.productImageType().name());

        final var aProductEntity = this.productJpaRepository.findById(aProductId).get();

        Assertions.assertTrue(aProductEntity.getCoverImage().isPresent());
    }

    @Test
    void givenAValidValuesWithGalleryType_whenCallsUploadProductImage_shouldUploadImage() {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aProductId = aProduct.getId().getValue();

        this.productJpaRepository.save(ProductJpaEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productJpaRepository.count());

        final var aCommand = UploadProductImageCommand.with(
                aProductId,
                Fixture.Products.imageGalleryTypeResource()
        );

        // when
        final var actualResult = this.uploadProductImageUseCase.execute(aCommand);

        // then
        Assertions.assertEquals(aProductId, actualResult.productId());
        Assertions.assertEquals(ProductImageType.GALLERY.name(), actualResult.productImageType().name());

        final var aProductEntity = this.productJpaRepository.findById(aProductId).get();

        Assertions.assertEquals(1, aProductEntity.getImages().size());
    }
}
