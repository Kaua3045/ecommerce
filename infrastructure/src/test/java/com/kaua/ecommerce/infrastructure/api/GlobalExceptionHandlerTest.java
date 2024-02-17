package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfCommand;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.validation.Error;
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

@ControllerTest(controllers = CustomerAPI.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UpdateCustomerCpfUseCase updateCustomerCpfUseCase;

    @MockBean
    private UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase;

    @MockBean
    private GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase;

    @MockBean
    private UpdateCustomerAddressUseCase updateCustomerAddressUseCase;

    @Test
    void testThrowDomainException() throws Exception {
        final var aCleanCpf = "81595915001";
        final var aAccountId = "123";

        final var expectedErrorMessage = "'cpf' invalid";

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/cpf", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerCpfCommand.class);

        Mockito.verify(updateCustomerCpfUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aCleanCpf, actualCmd.cpf());
    }

    @Test
    void testThrowNotFoundException() throws Exception {
        final var aCleanCpf = "81595915001";
        final var aAccountId = "123";

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

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
    void testThrowTransactionFailureOptimisticLockException() throws Exception {
        final var aCleanCpf = "81595915001";
        final var aAccountId = "123";

        final var expectedErrorMessage = "An optimistic lock exception occurred while processing the transaction";

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenThrow(TransactionFailureException.with(new Error("Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)")));

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/cpf", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(updateCustomerCpfUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void testThrowTransactionFailureWithTransactionUnexpectedException() throws Exception {
        final var aCleanCpf = "81595915001";
        final var aAccountId = "123";

        final var expectedErrorMessage = "An error occurred while processing the transaction";

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenThrow(TransactionFailureException.with(new Error(expectedErrorMessage)));

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/cpf", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(updateCustomerCpfUseCase, Mockito.times(1)).execute(Mockito.any());
    }
}
