package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryOutput;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.inventory.models.CreateInventoryInput;
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

    @Test
    void givenAValidInput_whenCallCreateInventory_thenReturnStatusOkAndIdAndSku() throws Exception {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aProductId = aInventory.getProductId();
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInput = new CreateInventoryInput(
                aProductId,
                aSku,
                aQuantity
        );

        Mockito.when(createInventoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateInventoryOutput.from(aInventory)));

        final var request = MockMvcRequestBuilders.post("/v1/inventories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.inventory_id", equalTo(aInventory.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_id", equalTo(aProductId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sku", equalTo(aInventory.getSku())));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateInventoryCommand.class);

        Mockito.verify(createInventoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aProductId, actualCmd.productId());
        Assertions.assertEquals(aSku, actualCmd.sku());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
    }

    @Test
    void givenAnInvalidInputNullProductId_whenCallCreateInventory_thenReturnDomainException() throws Exception {
        final String aProductId = null;
        final var aSku = "sku";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");

        final var aInput = new CreateInventoryInput(
                aProductId,
                aSku,
                aQuantity
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
        Assertions.assertEquals(aSku, actualCmd.sku());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
    }

    @Test
    void givenAnInvalidInputNullSku_whenCallCreateInventory_thenReturnDomainException() throws Exception {
        final var aProductId = "productId";
        final String aSku = null;
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");

        final var aInput = new CreateInventoryInput(
                aProductId,
                aSku,
                aQuantity
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
        Assertions.assertEquals(aSku, actualCmd.sku());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
    }

    @Test
    void givenAnInvalidInputQuantityLessThanZero_whenCallCreateInventory_thenReturnDomainException() throws Exception {
        final var aProductId = "productId";
        final var aSku = "sku";
        final var aQuantity = -1;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);

        final var aInput = new CreateInventoryInput(
                aProductId,
                aSku,
                aQuantity
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
        Assertions.assertEquals(aSku, actualCmd.sku());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
    }
}
