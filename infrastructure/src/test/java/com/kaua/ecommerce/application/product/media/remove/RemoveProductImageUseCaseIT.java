package com.kaua.ecommerce.application.product.media.remove;

import com.kaua.ecommerce.application.usecases.product.media.remove.RemoveProductImageCommand;
import com.kaua.ecommerce.application.usecases.product.media.remove.RemoveProductImageUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class RemoveProductImageUseCaseIT {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private RemoveProductImageUseCase removeProductImageUseCase;

    @Test
    void givenAValidValuesWithBannerType_whenCallsRemoveProductImage_shouldBeOk() {
        // given
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        final var aProductId = aProduct.getId().getValue();
        aProduct.changeBannerImage(aProductImage);

        this.productJpaRepository.save(ProductJpaEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productJpaRepository.count());

        final var aCommand = RemoveProductImageCommand.with(
                aProductId,
                aProductImage.getLocation()
        );

        // when
        Assertions.assertDoesNotThrow(() -> this.removeProductImageUseCase.execute(aCommand));

        // then
        final var aProductEntity = this.productJpaRepository.findById(aProductId).get();

        Assertions.assertTrue(aProductEntity.getBannerImage().isEmpty());
    }

    @Test
    void givenAValidValuesWithGalleryType_whenCallsRemoveProductImage_shouldBeOk() {
        // given
        final var aProductImage = Fixture.Products.productImage(ProductImageType.GALLERY);
        final var aProduct = Fixture.Products.tshirt();
        final var aProductId = aProduct.getId().getValue();
        aProduct.addImage(aProductImage);

        this.productJpaRepository.save(ProductJpaEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productJpaRepository.count());

        final var aCommand = RemoveProductImageCommand.with(
                aProductId,
                aProductImage.getLocation()
        );

        // when
        Assertions.assertDoesNotThrow(() -> this.removeProductImageUseCase.execute(aCommand));

        // then
        final var aProductEntity = this.productJpaRepository.findById(aProductId).get();

        Assertions.assertTrue(aProductEntity.getImages().isEmpty());
    }
}
