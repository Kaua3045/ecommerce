package com.kaua.ecommerce.application.product.delete;

import com.kaua.ecommerce.application.usecases.product.delete.DeleteProductUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class DeleteProductUseCaseIT {

    @Autowired
    private DeleteProductUseCase deleteProductUseCase;

    @Autowired
    private ProductJpaRepository productRepository;

    @Test
    void givenAValidProductId_whenCallsDeleteProductUseCase_thenProductShouldBeDeleted() {
        final var aProduct = Fixture.Products.tshirt();
        this.productRepository.save(ProductJpaEntity.toEntity(aProduct));

        final var aProductId = aProduct.getId().getValue();

        Assertions.assertEquals(1, this.productRepository.count());

        Assertions.assertDoesNotThrow(() -> this.deleteProductUseCase.execute(aProductId));

        Assertions.assertEquals(1, this.productRepository.count());

        final var aPersistedProduct = this.productRepository.findById(aProductId);

        Assertions.assertEquals(ProductStatus.DELETED, aPersistedProduct.get().getStatus());
    }

    @Test
    void givenAnInvalidProductId_whenCallsDeleteProductUseCase_thenShouldBeOk() {
        final var aProductId = ProductID.unique().getValue();

        Assertions.assertEquals(0, this.productRepository.count());

        Assertions.assertDoesNotThrow(() -> this.deleteProductUseCase.execute(aProductId));

        Assertions.assertEquals(0, this.productRepository.count());

        final var aPersistedProduct = this.productRepository.findById(aProductId);

        Assertions.assertTrue(aPersistedProduct.isEmpty());
    }
}
