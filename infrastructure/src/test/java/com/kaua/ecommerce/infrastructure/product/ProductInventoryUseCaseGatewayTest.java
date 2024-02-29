package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.clean.CleanInventoriesByProductIdUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.application.usecases.inventory.rollback.RollbackInventoryBySkuCommand;
import com.kaua.ecommerce.application.usecases.inventory.rollback.RollbackInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.exceptions.ProductInventoryException;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaRepository;
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
    private InventoryMovementJpaRepository inventoryMovementJpaRepository;

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
                Mockito.mock(RemoveInventoryBySkuUseCase.class),
                Mockito.mock(RollbackInventoryBySkuUseCase.class));

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
                aMockRemoveInventoryBySkuUseCase,
                Mockito.mock(RollbackInventoryBySkuUseCase.class));

        final var aSku = "invalid-sku";

        Mockito.doThrow(new RuntimeException()).when(aMockRemoveInventoryBySkuUseCase).execute(aSku);

        final var aOutput = aMockProductInventoryGateway.deleteInventoryBySku(aSku);

        Assertions.assertTrue(aOutput.isLeft());
        Assertions.assertEquals("error on delete inventory by sku %s".formatted(aSku), aOutput.getLeft().getErrors().get(0).message());
    }

    @Test
    void givenAValidSkuAndProductId_whenCallRollbackInventoryBySkuAndProductId_thenShouldRollbackInventory() {
        final var aProduct = Fixture.Products.book();
        this.productJpaRepository.saveAndFlush(ProductJpaEntity.toEntity(aProduct));

        final var aProductId = aProduct.getId().getValue();
        final var aProductSku = aProduct.getAttributes().stream().findFirst().get().getSku();
        final var aQuantity = 5;

        this.inventoryMovementJpaRepository.save(InventoryMovementJpaEntity.toEntity(
                InventoryMovement.newInventoryMovement(
                        InventoryID.unique(),
                        aProductSku,
                        aQuantity,
                        InventoryMovementStatus.REMOVED
                )
        ));

        Assertions.assertEquals(1, this.inventoryMovementJpaRepository.count());
        Assertions.assertEquals(0, this.inventoryJpaRepository.count());

        this.productInventoryGateway.rollbackInventoryBySkuAndProductId(aProductSku, aProductId);

        Assertions.assertEquals(1, this.inventoryJpaRepository.count());
        Assertions.assertEquals(1, this.inventoryMovementJpaRepository.count());

        Assertions.assertEquals(InventoryMovementStatus.IN,
                this.inventoryMovementJpaRepository.findAll().get(0).getMovementType());
    }

    @Test
    void givenAnInvalidSkuAndProductId_whenCallRollbackInventoryBySku_thenThrowProductInventoryException() {
        final var aMockRollbackInventoryBySkuUseCase = Mockito.mock(RollbackInventoryBySkuUseCase.class);
        final var aMockProductInventoryGateway = new ProductInventoryUseCaseGateway(
                Mockito.mock(CreateInventoryUseCase.class),
                Mockito.mock(CleanInventoriesByProductIdUseCase.class),
                Mockito.mock(RemoveInventoryBySkuUseCase.class),
                aMockRollbackInventoryBySkuUseCase);

        final var aSku = "invalid-sku";
        final var aProductId = "invalid-id";

        final var aCommand = RollbackInventoryBySkuCommand.with(aSku, aProductId);

        Mockito.doThrow(new RuntimeException("Test")).when(aMockRollbackInventoryBySkuUseCase).execute(aCommand);

        final var aOutput = Assertions.assertThrows(ProductInventoryException.class,
                () -> aMockProductInventoryGateway.rollbackInventoryBySkuAndProductId(aSku, aProductId));

        Assertions.assertEquals("error on rollback inventory by sku %s and product id %s".formatted(aSku, aProductId), aOutput.getMessage());
    }
}
