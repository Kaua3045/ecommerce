package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.outputs.CreateInventoryOutput;
import com.kaua.ecommerce.application.usecases.inventory.delete.clean.CleanInventoriesByProductIdUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.application.usecases.inventory.increase.IncreaseInventoryQuantityCommand;
import com.kaua.ecommerce.application.usecases.inventory.increase.IncreaseInventoryQuantityOutput;
import com.kaua.ecommerce.application.usecases.inventory.increase.IncreaseInventoryQuantityUseCase;
import com.kaua.ecommerce.application.usecases.inventory.rollback.RollbackInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.inventory.models.CreateInventoryInput;
import com.kaua.ecommerce.infrastructure.inventory.models.CreateInventoryInputParams;
import com.kaua.ecommerce.infrastructure.inventory.models.IncreaseInventoryQuantityInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ControllerTest(controllers = InventoryAPI.class)
public class InventoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateInventoryUseCase createInventoryUseCase;

    @MockBean
    private CleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase;

    @MockBean
    private RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;

    @MockBean
    private RollbackInventoryBySkuUseCase rollbackInventoryBySkuUseCase;

    @MockBean
    private IncreaseInventoryQuantityUseCase increaseInventoryQuantityUseCase;

    @Test
    void givenAValidInput_whenCallCreateInventory_thenReturnStatusOkAndIdAndSku() throws Exception {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aProductId = aInventory.getProductId();
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInputParams = new CreateInventoryInputParams(
                aSku,
                aQuantity
        );

        final var aInput = new CreateInventoryInput(
                aProductId,
                List.of(aInputParams)
        );

        Mockito.when(createInventoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateInventoryOutput.from(aProductId, Set.of(aInventory))));

        final var request = MockMvcRequestBuilders.post("/v1/inventories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_id", equalTo(aProductId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.inventories[0].inventory_id", equalTo(aInventory.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.inventories[0].sku", equalTo(aInventory.getSku())));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateInventoryCommand.class);

        Mockito.verify(createInventoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aProductId, actualCmd.productId());
        Assertions.assertEquals(1, actualCmd.inventoryParams().size());
    }

    @Test
    void givenAnInvalidInputNullProductId_whenCallCreateInventory_thenReturnDomainException() throws Exception {
        final String aProductId = null;
        final var aSku = "sku";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");

        final var aInputParams = new CreateInventoryInputParams(
                aSku,
                aQuantity
        );

        final var aInput = new CreateInventoryInput(
                aProductId,
                List.of(aInputParams)
        );

        Mockito.when(createInventoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/inventories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateInventoryCommand.class);

        Mockito.verify(createInventoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aProductId, actualCmd.productId());
        Assertions.assertEquals(1, actualCmd.inventoryParams().size());
    }

    @Test
    void givenAnInvalidInputNullSku_whenCallCreateInventory_thenReturnDomainException() throws Exception {
        final var aProductId = "productId";
        final String aSku = null;
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");

        final var aInputParams = new CreateInventoryInputParams(
                aSku,
                aQuantity
        );

        final var aInput = new CreateInventoryInput(
                aProductId,
                List.of(aInputParams)
        );

        Mockito.when(createInventoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/inventories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateInventoryCommand.class);

        Mockito.verify(createInventoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aProductId, actualCmd.productId());
        Assertions.assertEquals(1, actualCmd.inventoryParams().size());
    }

    @Test
    void givenAnInvalidInputQuantityLessThanZero_whenCallCreateInventory_thenReturnDomainException() throws Exception {
        final var aProductId = "productId";
        final var aSku = "sku";
        final var aQuantity = -1;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);

        final var aInputParams = new CreateInventoryInputParams(
                aSku,
                aQuantity
        );

        final var aInput = new CreateInventoryInput(
                aProductId,
                List.of(aInputParams)
        );

        Mockito.when(createInventoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/inventories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateInventoryCommand.class);

        Mockito.verify(createInventoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aProductId, actualCmd.productId());
        Assertions.assertEquals(1, actualCmd.inventoryParams().size());
    }

    @Test
    void givenAValidProductId_whenCallDeleteInventoriesByProductId_thenReturnStatusOk() throws Exception {
        final var aProductId = ProductID.unique().getValue();

        Mockito.doNothing().when(cleanInventoriesByProductIdUseCase).execute(aProductId);

        final var request = MockMvcRequestBuilders.delete("/v1/inventories/{productId}", aProductId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(cleanInventoriesByProductIdUseCase, Mockito.times(1)).execute(aProductId);
    }

    @Test
    void givenAValidSku_whenCallDeleteInventoryBySku_thenReturnStatusOk() throws Exception {
        final var aSku = Fixture.createSku("sku");

        Mockito.doNothing().when(removeInventoryBySkuUseCase).execute(aSku);

        final var request = MockMvcRequestBuilders.delete("/v1/inventories/sku/{sku}", aSku)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(removeInventoryBySkuUseCase, Mockito.times(1)).execute(aSku);
    }

    @Test
    void givenAValidProductIdAndSku_whenCallRollbackInventoryBySku_thenReturnStatusOk() throws Exception {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = Fixture.createSku("sku");

        Mockito.doNothing().when(rollbackInventoryBySkuUseCase).execute(Mockito.any());

        final var request = MockMvcRequestBuilders.post("/v1/inventories/rollback/{productId}/{sku}", aProductId, aSku)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(rollbackInventoryBySkuUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidInput_whenCallIncreaseInventoryQuantity_thenReturnStatusOkAndIdAndSkuAndProductId() throws Exception {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInput = new IncreaseInventoryQuantityInput(
                aQuantity
        );

        Mockito.when(increaseInventoryQuantityUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(IncreaseInventoryQuantityOutput.from(aInventory)));

        final var request = MockMvcRequestBuilders.patch("/v1/inventories/increase/{sku}", aSku)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_id", equalTo(aInventory.getProductId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.inventory_id", equalTo(aInventory.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sku", equalTo(aInventory.getSku())));

        final var cmdCaptor = ArgumentCaptor.forClass(IncreaseInventoryQuantityCommand.class);

        Mockito.verify(increaseInventoryQuantityUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aSku, actualCmd.sku());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
    }

    @Test
    void givenAnInvalidInputQuantity_whenCallIncreaseInventoryQuantity_thenReturnDomainException() throws Exception {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aSku = "sku";
        final var aQuantity = 0;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", 0);

        final var aInput = new IncreaseInventoryQuantityInput(
                aQuantity
        );

        Mockito.when(increaseInventoryQuantityUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/inventories/increase/{sku}", aSku)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(IncreaseInventoryQuantityCommand.class);

        Mockito.verify(increaseInventoryQuantityUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aSku, actualCmd.sku());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
    }
}
