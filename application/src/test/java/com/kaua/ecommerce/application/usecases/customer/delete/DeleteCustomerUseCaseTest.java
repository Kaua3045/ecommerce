package com.kaua.ecommerce.application.usecases.customer.delete;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class DeleteCustomerUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private CacheGateway<Customer> customerCacheGateway;

    @InjectMocks
    private DefaultDeleteCustomerUseCase useCase;

    @Test
    void givenAValidAccountId_whenCallDeleteCustomer_shouldBeOk() {
        final var aCustomer = Customer.newCustomer(
                "123",
                "Teste",
                "Testes",
                "teste.testes@tsss.com"
        );

        Mockito.doNothing().when(customerGateway).deleteById(aCustomer.getAccountId());
        Mockito.doNothing().when(customerCacheGateway).delete(aCustomer.getAccountId());

        Assertions.assertDoesNotThrow(() -> useCase.execute(aCustomer.getAccountId()));
        Mockito.verify(customerGateway, Mockito.times(1)).deleteById(aCustomer.getAccountId());
        Mockito.verify(customerCacheGateway, Mockito.times(1)).delete(aCustomer.getAccountId());
    }

    @Test
    void givenAnInvalidAccountId_whenCallDeleteCustomer_shouldBeOk() {
        final var aAccountId = "123";

        Mockito.doNothing().when(customerGateway).deleteById(aAccountId);
        Mockito.doNothing().when(customerCacheGateway).delete(aAccountId);

        Assertions.assertDoesNotThrow(() -> useCase.execute(aAccountId));
        Mockito.verify(customerGateway, Mockito.times(1)).deleteById(aAccountId);
        Mockito.verify(customerCacheGateway, Mockito.times(1)).delete(aAccountId);
    }
}
