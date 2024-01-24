package com.kaua.ecommerce.application.product.media.upload;

import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageCommand;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
public class UploadProductImageUseCaseIT {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private UploadProductImageUseCase uploadProductImageUseCase;

    @Test
    void givenAValidValuesWithBannerType_whenCallsUploadProductImage_shouldUploadImage() {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aProductId = aProduct.getId().getValue();

        this.productJpaRepository.save(ProductJpaEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productJpaRepository.count());

        final var aCommand = UploadProductImageCommand.with(
                aProductId,
                List.of(Fixture.Products.imageBannerTypeResource())
        );

        // when
        final var actualResult = this.uploadProductImageUseCase.execute(aCommand);

        // then
        Assertions.assertEquals(aProductId, actualResult.productId());

        final var aProductEntity = this.productJpaRepository.findById(aProductId).get();

        Assertions.assertTrue(aProductEntity.getBannerImage().isPresent());
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
                List.of(Fixture.Products.imageGalleryTypeResource())
        );

        // when
        final var actualResult = this.uploadProductImageUseCase.execute(aCommand);

        // then
        Assertions.assertEquals(aProductId, actualResult.productId());

        final var aProductEntity = this.productJpaRepository.findById(aProductId).get();

        Assertions.assertEquals(1, aProductEntity.getImages().size());
    }
}
