package com.kaua.ecommerce.application.product.update.status;

import com.kaua.ecommerce.application.usecases.product.update.status.UpdateProductStatusCommand;
import com.kaua.ecommerce.application.usecases.product.update.status.UpdateProductStatusUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class UpdateProductStatusUseCaseIT {

    @Autowired
    private UpdateProductStatusUseCase updateProductStatusUseCase;

    @Autowired
    private ProductJpaEntityRepository productRepository;

    @Test
    void givenAValidValues_whenCallsUpdateProductStatusUseCase_thenProductStatusShouldBeUpdated() {
        final var aProduct = Fixture.Products.tshirt();
        this.productRepository.save(ProductJpaEntity.toEntity(aProduct));

        final var aStatus = ProductStatus.INACTIVE;

        final var aCommand = UpdateProductStatusCommand.with(
                aProduct.getId().getValue(),
                aStatus.name()
        );

        final var aProductUpdatedAt = aProduct.getUpdatedAt();

        Assertions.assertEquals(1, this.productRepository.count());

        final var aResult = this.updateProductStatusUseCase.execute(aCommand);

        Assertions.assertEquals(1, this.productRepository.count());

        Assertions.assertNotNull(aResult.id());

        final var aPersistedProduct = this.productRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aResult.id(), aPersistedProduct.getId());
        Assertions.assertEquals(aStatus, aPersistedProduct.getStatus());
        Assertions.assertTrue(aProductUpdatedAt.isBefore(aPersistedProduct.getUpdatedAt()));
    }
}
