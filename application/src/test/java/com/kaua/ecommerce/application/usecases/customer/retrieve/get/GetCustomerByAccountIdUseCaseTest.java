package com.kaua.ecommerce.application.usecases.customer.retrieve.get;

import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GetCustomerByAccountIdUseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @InjectMocks
    private DefaultGetCustomerByAccountIdUseCase useCase;

    @Test
    void givenAValidAccountId_whenCallGetCustomerByAccountId_shouldReturnCustomer() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;

        Mockito.when(customerGateway.findByAccountId(aCustomer.getAccountId()))
                .thenReturn(Optional.of(aCustomer));

        final var output = useCase.execute(aCustomer.getAccountId());

        Assertions.assertEquals(aCustomer.getId().getValue(), output.id());
        Assertions.assertEquals(aCustomer.getAccountId(), output.accountId());
        Assertions.assertEquals(aCustomer.getFirstName(), output.firstName());
        Assertions.assertEquals(aCustomer.getLastName(), output.lastName());
        Assertions.assertEquals(aCustomer.getEmail(), output.email());
        Assertions.assertEquals(aCustomer.getCpf().getFormattedCpf(), output.cpf());
        Assertions.assertEquals(aCustomer.getTelephone().getValue(), output.telephone());
        Assertions.assertEquals(aCustomer.getAddress().getStreet(), output.address().street());
        Assertions.assertEquals(aCustomer.getAddress().getNumber(), output.address().number());
        Assertions.assertEquals(aCustomer.getAddress().getComplement(), output.address().complement());
        Assertions.assertEquals(aCustomer.getAddress().getDistrict(), output.address().district());
        Assertions.assertEquals(aCustomer.getAddress().getCity(), output.address().city());
        Assertions.assertEquals(aCustomer.getAddress().getState(), output.address().state());
        Assertions.assertEquals(aCustomer.getAddress().getZipCode(), output.address().zipCode());
        Assertions.assertEquals(aCustomer.getCreatedAt(), output.createdAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), output.updatedAt());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aCustomer.getAccountId());
    }

    @Test
    void givenAValidAccountIdWithNullCpfAndTelephoneAndAddress_whenCallGetCustomerByAccountId_shouldReturnCustomer() {
        final var aCustomer = Fixture.Customers.customerDefault;

        Mockito.when(customerGateway.findByAccountId(aCustomer.getAccountId()))
                .thenReturn(Optional.of(aCustomer));

        final var output = useCase.execute(aCustomer.getAccountId());

        Assertions.assertEquals(aCustomer.getId().getValue(), output.id());
        Assertions.assertEquals(aCustomer.getAccountId(), output.accountId());
        Assertions.assertEquals(aCustomer.getFirstName(), output.firstName());
        Assertions.assertEquals(aCustomer.getLastName(), output.lastName());
        Assertions.assertEquals(aCustomer.getEmail(), output.email());
        Assertions.assertNull(output.cpf());
        Assertions.assertNull(output.telephone());
        Assertions.assertNull(output.address());
        Assertions.assertEquals(aCustomer.getCreatedAt(), output.createdAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), output.updatedAt());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aCustomer.getAccountId());
    }

    @Test
    void givenAnInvalidAccountId_whenCallGetCustomerByAccountId_shouldThrowNotFoundException() {
        final var aAccountId = "123";
        final var expectedErrorMessage = "Customer with id 123 was not found";

        Mockito.when(customerGateway.findByAccountId(aAccountId))
                .thenReturn(Optional.empty());

        final var output = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aAccountId));

        Assertions.assertEquals(expectedErrorMessage, output.getMessage());
    }
}
