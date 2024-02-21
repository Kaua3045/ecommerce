package com.kaua.ecommerce.application.usecases.inventory.create;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommandParams;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class CreateInventoryUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private DefaultCreateInventoryUseCase createInventoryUseCase;

    @Test
    void givenAValidCommand_whenCallExecute_thenShouldCreateInventories() {
        final var aProductId = "1";
        final var aSkuOne = "sku-one";
        final var aQuantityOne = 10;
        final var aSkuTwo = "sku-two";
        final var aQuantityTwo = 20;

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSkuOne, aQuantityOne));
        aParams.add(CreateInventoryCommandParams.with(aSkuTwo, aQuantityTwo));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        Mockito.when(this.inventoryGateway.existsBySkus(Mockito.any())).thenReturn(List.of());
        Mockito.when(this.inventoryGateway.createInBatch(Mockito.any())).then(returnsFirstArg());

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.inventories());
        Assertions.assertNotNull(aOutput.productId());
        Assertions.assertEquals(2, aOutput.inventories().size());

        Mockito.verify(inventoryGateway, Mockito.times(1))
                .existsBySkus(Mockito.any());
        Mockito.verify(inventoryGateway, Mockito.times(1)).createInBatch(argThat(aCmd ->
                Objects.equals(2, aCmd.size())));
    }

    @Test
    void givenAValidCommandButOneSkuIsExists_whenCallExecute_thenShouldCreateInventoriesSizeIsOne() {
        final var aProductId = "1";
        final var aSkuOne = "sku-one";
        final var aQuantityOne = 10;
        final var aSkuTwo = "sku-two";
        final var aQuantityTwo = 20;

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSkuOne, aQuantityOne));
        aParams.add(CreateInventoryCommandParams.with(aSkuTwo, aQuantityTwo));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        Mockito.when(this.inventoryGateway.existsBySkus(Mockito.any()))
                .thenReturn(List.of(aSkuTwo));
        Mockito.when(this.inventoryGateway.createInBatch(Mockito.any())).then(returnsFirstArg());

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.inventories());
        Assertions.assertNotNull(aOutput.productId());
        Assertions.assertEquals(1, aOutput.inventories().size());

        Mockito.verify(inventoryGateway, Mockito.times(1))
                .existsBySkus(Mockito.any());
        Mockito.verify(inventoryGateway, Mockito.times(1)).createInBatch(argThat(aCmd ->
                Objects.equals(1, aCmd.size())));
    }

    @Test
    void givenAnInvalidAllSkuAlreadyExists_whenCallExecute_shouldThrowDomainException() {
        final var aProductId = "1";
        final var aSkuOne = "sku-one";
        final var aQuantityOne = 10;
        final var aSkuTwo = "sku-two";
        final var aQuantityTwo = 20;

        final var expectedErrorMessage = "All skus already exists";

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSkuOne, aQuantityOne));
        aParams.add(CreateInventoryCommandParams.with(aSkuTwo, aQuantityTwo));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        Mockito.when(this.inventoryGateway.existsBySkus(Mockito.any()))
                .thenReturn(List.of(aSkuOne, aSkuTwo));

        final var aOutput = Assertions.assertThrows(DomainException.class,
                () -> this.createInventoryUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(1))
                .existsBySkus(Mockito.any());
        Mockito.verify(inventoryGateway, Mockito.times(0)).createInBatch(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankProductId_whenCallExecute_shouldReturnDomainException() {
        final var aProductId = " ";
        final var aSku = "sku";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSku, aQuantity));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );


        Mockito.when(this.inventoryGateway.existsBySkus(Mockito.any()))
                .thenReturn(List.of());

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(1)).existsBySkus(List.of(aSku));
        Mockito.verify(inventoryGateway, Mockito.times(0)).createInBatch(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankSku_whenCallExecute_shouldReturnDomainException() {
        final var aProductId = "1";
        final var aSku = " ";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSku, aQuantity));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        Mockito.when(this.inventoryGateway.existsBySkus(Mockito.any()))
                .thenReturn(List.of());

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(1))
                .existsBySkus(List.of(aSku));
        Mockito.verify(inventoryGateway, Mockito.times(0)).createInBatch(Mockito.any());
    }

    @Test
    void givenAnInvalidQuantityLessThanZero_whenCallExecute_shouldReturnDomainException() {
        final var aProductId = "1";
        final var aSku = "sku";
        final var aQuantity = -1;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);

        final var aParams = new HashSet<CreateInventoryCommandParams>();
        aParams.add(CreateInventoryCommandParams.with(aSku, aQuantity));

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aParams
        );

        Mockito.when(this.inventoryGateway.existsBySkus(Mockito.any()))
                .thenReturn(List.of());

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(1)).existsBySkus(List.of(aSku));
        Mockito.verify(inventoryGateway, Mockito.times(0)).createInBatch(Mockito.any());
    }

    @Test
    void givenAnInvalidNullParams_whenCallExecute_shouldThrowDomainException() {
        final var aProductId = "1";

        final var expectedErrorMessage = "Inventory params cannot be null or empty";

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                null
        );

        final var aOutput = Assertions.assertThrows(DomainException.class,
                () -> this.createInventoryUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(0))
                .existsBySkus(Mockito.any());
        Mockito.verify(inventoryGateway, Mockito.times(0)).createInBatch(Mockito.any());
    }

    @Test
    void givenAnInvalidEmptyParams_whenCallExecute_shouldThrowDomainException() {
        final var aProductId = "1";

        final var expectedErrorMessage = "Inventory params cannot be null or empty";

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                Set.of()
        );

        final var aOutput = Assertions.assertThrows(DomainException.class,
                () -> this.createInventoryUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(0))
                .existsBySkus(Mockito.any());
        Mockito.verify(inventoryGateway, Mockito.times(0)).createInBatch(Mockito.any());
    }
}
