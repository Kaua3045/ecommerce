package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfCommand;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfOutput;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerCpfInput;
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

@ControllerTest(controllers = CustomerAPI.class)
public class CustomerAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UpdateCustomerCpfUseCase updateCustomerCpfUseCase;

    @Test
    void givenAValidInput_whenCallUpdateCpf_thenReturnStatusOkAndAccountId() throws Exception {
        final var aCustomer = Customer.newCustomer(
                "123",
                "test",
                "Testes",
                "test.testes@tss.com"
        );

        final var aCleanCpf = "81595915001";
        final var aAccountId = aCustomer.getAccountId();

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCustomerCpfOutput.from(aCustomer)));

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/cpf", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId", equalTo(aAccountId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerCpfCommand.class);

        Mockito.verify(updateCustomerCpfUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aCleanCpf, actualCmd.cpf());
    }

    @Test
    void givenAnInvalidAccountId_whenCallUpdateCpf_thenThrowsNotFoundException() throws Exception {
        final var aCleanCpf = "81595915001";
        final var aAccountId = "123";

        final var expectedErrorMessage = "Customer with id 123 was not found";

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Customer.class, aAccountId).get());

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/cpf", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerCpfCommand.class);

        Mockito.verify(updateCustomerCpfUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aCleanCpf, actualCmd.cpf());
    }

    @Test
    void givenAnInvalidCpf_whenCallUpdateCpf_thenReturnDomainException() throws Exception {
        final var aCustomer = Customer.newCustomer(
                "123",
                "test",
                "Testes",
                "test.testes@tss.com"
        );

        final var aCleanCpf = "815959150011";
        final var aAccountId = aCustomer.getAccountId();

        final var expectedErrorMessage = "'cpf' invalid";

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/cpf", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerCpfCommand.class);

        Mockito.verify(updateCustomerCpfUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aCleanCpf, actualCmd.cpf());
    }
}