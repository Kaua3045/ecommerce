package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdOutput;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfCommand;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfOutput;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneCommand;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneOutput;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.Telephone;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerCpfInput;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerTelephoneInput;
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

    @MockBean
    private UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase;

    @MockBean
    private GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase;

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

    @Test
    void givenAValidInput_whenCallUpdateTelephone_thenReturnStatusOkAndAccountId() throws Exception {
        final var aCustomer = Customer.newCustomer(
                "123",
                "test",
                "Testes",
                "test.testes@tss.com"
        );

        final var aTelephone = "+11234567890";
        final var aAccountId = aCustomer.getAccountId();

        final var aInput = new UpdateCustomerTelephoneInput(aTelephone);

        Mockito.when(updateCustomerTelephoneUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCustomerTelephoneOutput.from(aCustomer)));

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/telephone", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId", equalTo(aAccountId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerTelephoneCommand.class);

        Mockito.verify(updateCustomerTelephoneUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aTelephone, actualCmd.telephone());
    }

    @Test
    void givenAnInvalidAccountId_whenCallUpdateTelephone_thenThrowsNotFoundException() throws Exception {
        final var aTelephone = "+11234567890";
        final var aAccountId = "123";

        final var expectedErrorMessage = "Customer with id 123 was not found";

        final var aInput = new UpdateCustomerTelephoneInput(aTelephone);

        Mockito.when(updateCustomerTelephoneUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Customer.class, aAccountId).get());

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/telephone", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerTelephoneCommand.class);

        Mockito.verify(updateCustomerTelephoneUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aTelephone, actualCmd.telephone());
    }

    @Test
    void givenAnInvalidTelephone_whenCallUpdateTelephone_thenReturnDomainException() throws Exception {
        final var aCustomer = Customer.newCustomer(
                "123",
                "test",
                "Testes",
                "test.testes@tss.com"
        );

        final var aTelephone = " ";
        final var aAccountId = aCustomer.getAccountId();

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("telephone");

        final var aInput = new UpdateCustomerTelephoneInput(aTelephone);

        Mockito.when(updateCustomerTelephoneUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/customers/{accountId}/telephone", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerTelephoneCommand.class);

        Mockito.verify(updateCustomerTelephoneUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aTelephone, actualCmd.telephone());
    }

    @Test
    void givenAValidAccountId_whenCallGetCustomer_shouldReturnStatusOkAndCustomer() throws Exception {
        final var aCustomer = Customer.newCustomer(
                "123",
                "test",
                "Testes",
                "test.testes@tss.com")
                .changeTelephone(Telephone.newTelephone("+15551234567"));

        final var aAccountId = aCustomer.getAccountId();
        final var expectedTelephone = "+1 555-123-4567";

        Mockito.when(getCustomerByAccountIdUseCase.execute(Mockito.any()))
                .thenReturn(GetCustomerByAccountIdOutput.from(aCustomer));

        final var request = MockMvcRequestBuilders.get("/customers/{accountId}", aAccountId)
                .param("locale", "US")
                .accept(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aCustomer.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", equalTo(aCustomer.getAccountId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_name", equalTo(aCustomer.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last_name", equalTo(aCustomer.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", equalTo(aCustomer.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf", equalTo(aCustomer.getCpf())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.telephone", equalTo(expectedTelephone)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created_at", equalTo(aCustomer.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updated_at", equalTo(aCustomer.getUpdatedAt().toString())));
    }

    @Test
    void givenAnInvalidAccountId_whenCallGetCustomer_shouldThrowNotFoundException() throws Exception {
        final var aAccountId = "123";
        final var expectedErrorMessage = "Customer with id 123 was not found";

        Mockito.when(getCustomerByAccountIdUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Customer.class, aAccountId).get());

        final var request = MockMvcRequestBuilders.get("/customers/{accountId}", aAccountId)
                .accept(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));
    }
}
