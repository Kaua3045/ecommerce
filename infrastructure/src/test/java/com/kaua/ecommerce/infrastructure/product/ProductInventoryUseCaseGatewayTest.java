package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
public class ProductInventoryUseCaseGatewayTest {

    @Autowired
    private InventoryJpaRepository inventoryJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private ProductInventoryGateway productInventoryGateway;

    @Test
    void givenAValidValues_whenCallCreateInventory_thenShouldCreateInventoriesToProductAttributes() {
        final var aProduct = Fixture.Products.book();
        this.productJpaRepository.saveAndFlush(ProductJpaEntity.toEntity(aProduct));

        final var aProductId = aProduct.getId().getValue();
        final var aProductSku = aProduct.getAttributes().stream().findFirst().get().getSku();
        final var aQuantity = 5;

        final var aInventoryParams = new ProductInventoryGateway.CreateInventoryParams(
                aProductSku,
                aQuantity
        );

        Assertions.assertEquals(1, this.productJpaRepository.count());
        Assertions.assertEquals(0, this.inventoryJpaRepository.count());

        final var aOutput = this.productInventoryGateway.createInventory(
                aProductId,
                List.of(aInventoryParams)
        );

        Assertions.assertEquals(1, this.inventoryJpaRepository.count());
        Assertions.assertTrue(aOutput.isRight());
    }

    @Test
    void givenAnInvalidQuantity_whenCallCreateInventory_thenReturnNotificationHandlerWithException() {
        final var aProduct = Fixture.Products.book();
        this.productJpaRepository.saveAndFlush(ProductJpaEntity.toEntity(aProduct));

        final var aProductId = aProduct.getId().getValue();
        final var aProductSku = aProduct.getAttributes().stream().findFirst().get().getSku();
        final var aQuantity = -1;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);

        final var aInventoryParams = new ProductInventoryGateway.CreateInventoryParams(
                aProductSku,
                aQuantity
        );

        Assertions.assertEquals(1, this.productJpaRepository.count());
        Assertions.assertEquals(0, this.inventoryJpaRepository.count());

        final var aOutput = this.productInventoryGateway.createInventory(
                aProductId,
                List.of(aInventoryParams)
        );

        Assertions.assertEquals(0, this.inventoryJpaRepository.count());
        Assertions.assertTrue(aOutput.isLeft());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getLeft().getErrors().get(0).message());
    }
}
