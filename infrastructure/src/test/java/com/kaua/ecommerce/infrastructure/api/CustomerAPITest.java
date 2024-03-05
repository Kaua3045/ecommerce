package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.application.gateways.responses.AddressResponse;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdOutput;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressCommand;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressOutput;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfCommand;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfOutput;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneCommand;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneOutput;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerAddressInput;
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

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ControllerTest(controllers = CustomerAPI.class)
public class CustomerAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AddressGateway addressGateway;

    @MockBean
    private UpdateCustomerCpfUseCase updateCustomerCpfUseCase;

    @MockBean
    private UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase;

    @MockBean
    private GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase;

    @MockBean
    private UpdateCustomerAddressUseCase updateCustomerAddressUseCase;

    @Test
    void givenAValidInput_whenCallUpdateCpf_thenReturnStatusOkAndAccountId() throws Exception {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aCleanCpf = "81595915001";
        final var aAccountId = aCustomer.getAccountId();

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCustomerCpfOutput.from(aCustomer)));

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/cpf", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", equalTo(aAccountId)));

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

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Customer.class, aAccountId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/cpf", aAccountId)
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
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aCleanCpf = "815959150011";
        final var aAccountId = aCustomer.getAccountId();

        final var expectedErrorMessage = "'cpf' invalid";

        final var aInput = new UpdateCustomerCpfInput(aCleanCpf);

        Mockito.when(updateCustomerCpfUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/cpf", aAccountId)
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
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aTelephone = "+11234567890";
        final var aAccountId = aCustomer.getAccountId();

        final var aInput = new UpdateCustomerTelephoneInput(aTelephone);

        Mockito.when(updateCustomerTelephoneUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCustomerTelephoneOutput.from(aCustomer)));

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/telephone", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", equalTo(aAccountId)));

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

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aInput = new UpdateCustomerTelephoneInput(aTelephone);

        Mockito.when(updateCustomerTelephoneUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Customer.class, aAccountId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/telephone", aAccountId)
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
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aTelephone = " ";
        final var aAccountId = aCustomer.getAccountId();

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("telephone");

        final var aInput = new UpdateCustomerTelephoneInput(aTelephone);

        Mockito.when(updateCustomerTelephoneUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/telephone", aAccountId)
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
        final var aCustomer = Fixture.Customers.customerWithAllParams;

        final var aAccountId = aCustomer.getAccountId();
        final var expectedTelephone = "+1 555-123-4567";
        final var expectedCpf = aCustomer.getCpf().get().getFormattedCpf();

        Mockito.when(getCustomerByAccountIdUseCase.execute(Mockito.any()))
                .thenReturn(GetCustomerByAccountIdOutput.from(aCustomer));

        final var request = MockMvcRequestBuilders.get("/v1/customers/{accountId}", aAccountId)
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf", equalTo(expectedCpf)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.telephone", equalTo(expectedTelephone)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.street", equalTo(aCustomer.getAddress().get().getStreet())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.number", equalTo(aCustomer.getAddress().get().getNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.complement", equalTo(aCustomer.getAddress().get().getComplement())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.district", equalTo(aCustomer.getAddress().get().getDistrict())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.city", equalTo(aCustomer.getAddress().get().getCity())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.state", equalTo(aCustomer.getAddress().get().getState())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.zip_code", equalTo(aCustomer.getAddress().get().getZipCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created_at", equalTo(aCustomer.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updated_at", equalTo(aCustomer.getUpdatedAt().toString())));
    }

    @Test
    void givenAnInvalidAccountId_whenCallGetCustomer_shouldThrowNotFoundException() throws Exception {
        final var aAccountId = "123";
        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        Mockito.when(getCustomerByAccountIdUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Customer.class, aAccountId).get());

        final var request = MockMvcRequestBuilders.get("/v1/customers/{accountId}", aAccountId)
                .accept(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAValidInput_whenCallUpdateAddress_thenReturnStatusOkAndAccountId() throws Exception {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aInput = getUpdateCustomerAddressInput();

        Mockito.when(addressGateway.findAddressByZipCodeInExternalService(Mockito.any()))
                        .thenReturn(Optional.of(getAddressResponse()));
        Mockito.when(updateCustomerAddressUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCustomerAddressOutput.from(aCustomer)));

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/address", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", equalTo(aAccountId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerAddressCommand.class);

        Mockito.verify(updateCustomerAddressUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aInput.street(), actualCmd.street());
        Assertions.assertEquals(aInput.number(), actualCmd.number());
        Assertions.assertEquals(aInput.complement(), actualCmd.complement());
        Assertions.assertEquals(aInput.district(), actualCmd.district());
        Assertions.assertEquals(aInput.city(), actualCmd.city());
        Assertions.assertEquals(aInput.state(), actualCmd.state());
        Assertions.assertEquals(aInput.zipCode(), actualCmd.zipCode());
    }

    @Test
    void givenAnInvalidAccountId_whenCallUpdateAddress_thenThrowsNotFoundException() throws Exception {
        final var aAccountId = "123";

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aInput = getUpdateCustomerAddressInput();

        Mockito.when(updateCustomerAddressUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Customer.class, aAccountId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/address", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerAddressCommand.class);

        Mockito.verify(updateCustomerAddressUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aInput.street(), actualCmd.street());
        Assertions.assertEquals(aInput.number(), actualCmd.number());
        Assertions.assertEquals(aInput.complement(), actualCmd.complement());
        Assertions.assertEquals(aInput.district(), actualCmd.district());
        Assertions.assertEquals(aInput.city(), actualCmd.city());
        Assertions.assertEquals(aInput.state(), actualCmd.state());
        Assertions.assertEquals(aInput.zipCode(), actualCmd.zipCode());
    }

    @Test
    void givenAnInvalidAddress_whenCallUpdateAddress_thenReturnDomainException() throws Exception {
        final var aCustomer = Fixture.Customers.customerDefault;

        final var aAccountId = aCustomer.getAccountId();
        final var aStreet = " ";
        final var aNumber = "0";
        final var aComplement = "Casa";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("street");

        final var aInput = new UpdateCustomerAddressInput(
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode

        );

        Mockito.when(addressGateway.findAddressByZipCodeInExternalService(Mockito.any()))
                .thenReturn(Optional.of(getAddressResponse()));
        Mockito.when(updateCustomerAddressUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/customers/{accountId}/address", aAccountId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCustomerAddressCommand.class);

        Mockito.verify(updateCustomerAddressUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aAccountId, actualCmd.accountId());
        Assertions.assertEquals(aInput.street(), actualCmd.street());
        Assertions.assertEquals(aInput.number(), actualCmd.number());
        Assertions.assertEquals(aInput.complement(), actualCmd.complement());
        Assertions.assertEquals(aInput.district(), actualCmd.district());
        Assertions.assertEquals(aInput.city(), actualCmd.city());
        Assertions.assertEquals(aInput.state(), actualCmd.state());
        Assertions.assertEquals(aInput.zipCode(), actualCmd.zipCode());
    }

    private UpdateCustomerAddressInput getUpdateCustomerAddressInput() {
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "0";
        final var aComplement = "Casa";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345678";

        return new UpdateCustomerAddressInput(
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode

        );
    }

    private AddressResponse getAddressResponse() {
        final var aStreet = "Rua dos Bobos";
        final var aComplement = "Casa";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345678";

        return new AddressResponse(
                aZipCode,
                aStreet,
                aComplement,
                aDistrict,
                aCity,
                aState

        );
    }
}
