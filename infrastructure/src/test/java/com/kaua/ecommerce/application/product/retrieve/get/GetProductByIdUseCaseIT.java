package com.kaua.ecommerce.application.product.retrieve.get;

import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.RoundingMode;

@IntegrationTest
public class GetProductByIdUseCaseIT {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private GetProductByIdUseCase getProductByIdUseCase;

    @Test
    void givenAValidProductId_whenCallGetProductByIdUseCase_thenProductIsReturned() {
        // given
        final var aProduct = Fixture.Products.book();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        this.productJpaRepository.save(ProductJpaEntity.toEntity(aProduct));

        // when
        final var aOutput = this.getProductByIdUseCase.execute(aProduct.getId().getValue());

        // then
        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.productId());
        Assertions.assertEquals(aProduct.getName(), aOutput.name());
        Assertions.assertEquals(aProduct.getDescription(), aOutput.description());
        Assertions.assertEquals(aProduct.getPrice().setScale(2, RoundingMode.HALF_UP), aOutput.price());
        Assertions.assertEquals(aProduct.getQuantity(), aOutput.quantity());
        Assertions.assertEquals(aProduct.getCategoryId().getValue(), aOutput.categoryId());
        Assertions.assertNotNull(aOutput.bannerImage());
        Assertions.assertEquals(aProduct.getBannerImage().get().getLocation(), aOutput.bannerImage().location());
        Assertions.assertNotNull(aOutput.galleryImages());
        Assertions.assertEquals(aOutput.galleryImages().size(), aProduct.getImages().size());
        Assertions.assertEquals(aOutput.attributes().size(), aProduct.getAttributes().size());
        Assertions.assertEquals(aProduct.getStatus().name(), aOutput.status());
        Assertions.assertEquals(aProduct.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aProduct.getVersion(), aOutput.version());
    }

    @Test
    void givenAnInvalidProductId_whenCallGetProductByIdUseCase_thenNotFoundExceptionIsThrown() {
        // given
        final var aProductId = "1";

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aProductId);

        // when
        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.getProductByIdUseCase.execute(aProductId));

        // then
        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }
}
