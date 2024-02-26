package com.kaua.ecommerce.application.inventory.create;

import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommandParams;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

@IntegrationTest
public class CreateInventoryUseCaseIT {

    @Autowired
    private CreateInventoryUseCase createInventoryUseCase;

    @Autowired
    private InventoryJpaRepository inventoryRepository;

    @Test
    void givenAValidValues_whenCallsCreateInventoryUseCase_thenInventoriesShouldBeCreated() {
        final var aProductId = ProductID.unique().getValue();
        final var aSkuOne = Fixture.createSku("teste");
        final var aQuantityOne = 10;
        final var aSkuTwo = Fixture.createSku("teste-two");
        final var aQuantityTwo = 20;

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSkuOne, aQuantityOne));
        aParams.add(CreateInventoryCommandParams.with(aSkuTwo, aQuantityTwo));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        Assertions.assertEquals(0, this.inventoryRepository.count());

        final var aResult = this.createInventoryUseCase.execute(aCommand).getRight();

        Assertions.assertEquals(2, this.inventoryRepository.count());

        Assertions.assertNotNull(aResult.productId());
        Assertions.assertEquals(aProductId, aResult.productId());
        Assertions.assertEquals(2, aResult.inventories().size());

        final var aPersistedInventories = this.inventoryRepository.findAll();

        final var aPersistedInventoryOne = aPersistedInventories.stream()
                .filter(inventory -> inventory.getSku().equals(aSkuOne))
                .findFirst()
                .get();

        Assertions.assertEquals(aProductId, aPersistedInventoryOne.getProductId());
        Assertions.assertEquals(aSkuOne, aPersistedInventoryOne.getSku());
        Assertions.assertEquals(aQuantityOne, aPersistedInventoryOne.getQuantity());

        final var aPersistedInventoryTwo = aPersistedInventories.stream()
                .filter(inventory -> inventory.getSku().equals(aSkuTwo))
                .findFirst()
                .get();

        Assertions.assertEquals(aProductId, aPersistedInventoryTwo.getProductId());
        Assertions.assertEquals(aSkuTwo, aPersistedInventoryTwo.getSku());
        Assertions.assertEquals(aQuantityTwo, aPersistedInventoryTwo.getQuantity());
    }

    @Test
    void givenAnInvalidProductId_whenCallsCreateInventoryUseCase_shouldReturnDomainException() {
        final var aProductId = " ";
        final var aSku = Fixture.createSku("teste");
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");
        final var expectedErrorCount = 1;

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSku, aQuantity));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        final var aResult = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.inventoryRepository.count());
    }

    @Test
    void givenAnInvalidSKU_whenCallsCreateInventoryUseCase_shouldReturnDomainException() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = " ";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");
        final var expectedErrorCount = 2;

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSku, aQuantity));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        final var aResult = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.inventoryRepository.count());
    }

    @Test
    void givenAnInvalidQuantityLessThanZero_whenCallsCreateInventoryUseCase_shouldReturnDomainException() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = Fixture.createSku("teste");
        final var aQuantity = -1;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);
        final var expectedErrorCount = 2;

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSku, aQuantity));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        final var aResult = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.inventoryRepository.count());
    }
}
