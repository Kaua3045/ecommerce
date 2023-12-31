package com.kaua.ecommerce.application.usecases.customer.retrieve.get;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class GetCustomerByAccountIdUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private CacheGateway<Customer> customerCacheGateway;

    @InjectMocks
    private DefaultGetCustomerByAccountIdUseCase useCase;

    @Test
    void givenAValidAccountId_whenCallGetCustomerByAccountIdInDatabase_shouldReturnCustomerAndSaveInCache() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;

        Mockito.when(customerCacheGateway.get(aCustomer.getAccountId()))
                .thenReturn(Optional.empty());
        Mockito.when(customerGateway.findByAccountId(aCustomer.getAccountId()))
                .thenReturn(Optional.of(aCustomer));

        final var output = useCase.execute(aCustomer.getAccountId());

        Assertions.assertEquals(aCustomer.getId().getValue(), output.id());
        Assertions.assertEquals(aCustomer.getAccountId(), output.accountId());
        Assertions.assertEquals(aCustomer.getFirstName(), output.firstName());
        Assertions.assertEquals(aCustomer.getLastName(), output.lastName());
        Assertions.assertEquals(aCustomer.getEmail(), output.email());
        Assertions.assertEquals(aCustomer.getCpf().get().getFormattedCpf(), output.cpf());
        Assertions.assertEquals(aCustomer.getTelephone().get().getValue(), output.telephone());
        Assertions.assertEquals(aCustomer.getAddress().get().getStreet(), output.address().street());
        Assertions.assertEquals(aCustomer.getAddress().get().getNumber(), output.address().number());
        Assertions.assertEquals(aCustomer.getAddress().get().getComplement(), output.address().complement());
        Assertions.assertEquals(aCustomer.getAddress().get().getDistrict(), output.address().district());
        Assertions.assertEquals(aCustomer.getAddress().get().getCity(), output.address().city());
        Assertions.assertEquals(aCustomer.getAddress().get().getState(), output.address().state());
        Assertions.assertEquals(aCustomer.getAddress().get().getZipCode(), output.address().zipCode());
        Assertions.assertEquals(aCustomer.getCreatedAt(), output.createdAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), output.updatedAt());

        Mockito.verify(customerCacheGateway, Mockito.times(1)).get(aCustomer.getAccountId());
        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aCustomer.getAccountId());
    }

    @Test
    void givenAValidAccountId_whenCallGetCustomerByAccountIdInCache_shouldReturnCustomer() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;

        Mockito.when(customerCacheGateway.get(aCustomer.getAccountId()))
                .thenReturn(Optional.of(aCustomer));

        final var output = useCase.execute(aCustomer.getAccountId());

        Assertions.assertEquals(aCustomer.getId().getValue(), output.id());
        Assertions.assertEquals(aCustomer.getAccountId(), output.accountId());
        Assertions.assertEquals(aCustomer.getFirstName(), output.firstName());
        Assertions.assertEquals(aCustomer.getLastName(), output.lastName());
        Assertions.assertEquals(aCustomer.getEmail(), output.email());
        Assertions.assertEquals(aCustomer.getCpf().get().getFormattedCpf(), output.cpf());
        Assertions.assertEquals(aCustomer.getTelephone().get().getValue(), output.telephone());
        Assertions.assertEquals(aCustomer.getAddress().get().getStreet(), output.address().street());
        Assertions.assertEquals(aCustomer.getAddress().get().getNumber(), output.address().number());
        Assertions.assertEquals(aCustomer.getAddress().get().getComplement(), output.address().complement());
        Assertions.assertEquals(aCustomer.getAddress().get().getDistrict(), output.address().district());
        Assertions.assertEquals(aCustomer.getAddress().get().getCity(), output.address().city());
        Assertions.assertEquals(aCustomer.getAddress().get().getState(), output.address().state());
        Assertions.assertEquals(aCustomer.getAddress().get().getZipCode(), output.address().zipCode());
        Assertions.assertEquals(aCustomer.getCreatedAt(), output.createdAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), output.updatedAt());

        Mockito.verify(customerCacheGateway, Mockito.times(1)).get(aCustomer.getAccountId());
        Mockito.verify(customerGateway, Mockito.times(0)).findByAccountId(aCustomer.getAccountId());
    }

    @Test
    void givenAValidAccountIdWithNullCpfAndTelephoneAndAddress_whenCallGetCustomerByAccountId_shouldReturnCustomer() {
        final var aCustomer = Fixture.Customers.customerDefault;

        Mockito.when(customerCacheGateway.get(aCustomer.getAccountId()))
                .thenReturn(Optional.empty());
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

        Mockito.when(customerCacheGateway.get(aAccountId))
                .thenReturn(Optional.empty());
        Mockito.when(customerGateway.findByAccountId(aAccountId))
                .thenReturn(Optional.empty());

        final var output = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aAccountId));

        Assertions.assertEquals(expectedErrorMessage, output.getMessage());
    }
}
