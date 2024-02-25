package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.clean.CleanInventoriesByProductIdUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

    @Test
    void givenAValidProductId_whenCallCleanInventoriesByProductId_thenShouldCleanInventories() {
        final var aProduct = Fixture.Products.book();
        this.productJpaRepository.saveAndFlush(ProductJpaEntity.toEntity(aProduct));

        final var aProductId = aProduct.getId().getValue();
        final var aProductSku = aProduct.getAttributes().stream().findFirst().get().getSku();
        final var aQuantity = 5;

        final var aInventoryParams = new ProductInventoryGateway.CreateInventoryParams(
                aProductSku,
                aQuantity
        );

        this.productInventoryGateway.createInventory(
                aProductId,
                List.of(aInventoryParams)
        );

        Assertions.assertEquals(1, this.inventoryJpaRepository.count());

        final var aOutput = this.productInventoryGateway.cleanInventoriesByProductId(aProductId);

        Assertions.assertEquals(0, this.inventoryJpaRepository.count());
        Assertions.assertTrue(aOutput.isRight());
    }

    @Test
    void givenAnInvalidProductId_whenCallCleanInventoriesByProductId_thenReturnNotificationHandlerWithException() {
        final var aMockCleanInventoriesByProductIdUseCase = Mockito.mock(CleanInventoriesByProductIdUseCase.class);
        final var aMockProductInventoryGateway = new ProductInventoryUseCaseGateway(
                Mockito.mock(CreateInventoryUseCase.class),
                aMockCleanInventoriesByProductIdUseCase,
                Mockito.mock(RemoveInventoryBySkuUseCase.class)
        );

        final var aProductId = "invalid-id";

        Mockito.doThrow(new RuntimeException()).when(aMockCleanInventoriesByProductIdUseCase).execute(aProductId);

        final var aOutput = aMockProductInventoryGateway.cleanInventoriesByProductId(aProductId);

        Assertions.assertTrue(aOutput.isLeft());
        Assertions.assertEquals("error on clean inventories by product id %s".formatted(aProductId), aOutput.getLeft().getErrors().get(0).message());
    }

    @Test
    void givenAValidSku_whenCallRemoveInventoryBySku_thenShouldRemoveInventory() {
        final var aProduct = Fixture.Products.book();
        this.productJpaRepository.saveAndFlush(ProductJpaEntity.toEntity(aProduct));

        final var aProductId = aProduct.getId().getValue();
        final var aProductSku = aProduct.getAttributes().stream().findFirst().get().getSku();
        final var aQuantity = 5;

        final var aInventoryParams = new ProductInventoryGateway.CreateInventoryParams(
                aProductSku,
                aQuantity
        );

        this.productInventoryGateway.createInventory(
                aProductId,
                List.of(aInventoryParams)
        );

        Assertions.assertEquals(1, this.inventoryJpaRepository.count());

        final var aOutput = this.productInventoryGateway.deleteInventoryBySku(aProductSku);

        Assertions.assertEquals(0, this.inventoryJpaRepository.count());
        Assertions.assertTrue(aOutput.isRight());
    }

    @Test
    void givenAnInvalidSku_whenCallRemoveInventoryBySku_thenReturnNotificationHandlerWithException() {
        final var aMockRemoveInventoryBySkuUseCase = Mockito.mock(RemoveInventoryBySkuUseCase.class);
        final var aMockProductInventoryGateway = new ProductInventoryUseCaseGateway(
                Mockito.mock(CreateInventoryUseCase.class),
                Mockito.mock(CleanInventoriesByProductIdUseCase.class),
                aMockRemoveInventoryBySkuUseCase
        );

        final var aSku = "invalid-sku";

        Mockito.doThrow(new RuntimeException()).when(aMockRemoveInventoryBySkuUseCase).execute(aSku);

        final var aOutput = aMockProductInventoryGateway.deleteInventoryBySku(aSku);

        Assertions.assertTrue(aOutput.isLeft());
        Assertions.assertEquals("error on delete inventory by sku %s".formatted(aSku), aOutput.getLeft().getErrors().get(0).message());
    }
}
