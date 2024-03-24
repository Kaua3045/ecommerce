package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdOutput;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
public class OrderCustomerGatewayImplTest {

    @Autowired
    private OrderCustomerGatewayImpl orderCustomerGatewayImpl;

    @MockBean
    private GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase;

    @Test
    void givenAValidAccountId_whenFindCustomerByAccountId_thenReturnCustomer() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aCustomerId = aCustomer.getAccountId();

        Mockito.when(getCustomerByAccountIdUseCase.execute(aCustomerId))
                .thenReturn(GetCustomerByAccountIdOutput.from(aCustomer));

        final var aOutput = orderCustomerGatewayImpl.findByCustomerId(aCustomerId);

        Assertions.assertTrue(aOutput.isPresent());
        Assertions.assertEquals(aCustomer.getAccountId(), aOutput.get().customerId());
        Assertions.assertEquals(aCustomer.getAddress().get().getZipCode(), aOutput.get().zipCode());
        Assertions.assertEquals(aCustomer.getAddress().get().getStreet(), aOutput.get().street());
        Assertions.assertEquals(aCustomer.getAddress().get().getNumber(), aOutput.get().number());
        Assertions.assertEquals(aCustomer.getAddress().get().getComplement(), aOutput.get().complement());
        Assertions.assertEquals(aCustomer.getAddress().get().getDistrict(), aOutput.get().district());
        Assertions.assertEquals(aCustomer.getAddress().get().getCity(), aOutput.get().city());
        Assertions.assertEquals(aCustomer.getAddress().get().getState(), aOutput.get().state());
    }

    @Test
    void givenAnInvalidAccountId_whenFindCustomerByAccountId_thenReturnEmpty() {
        final var aCustomerId = "invalid-account-id";

        Mockito.when(getCustomerByAccountIdUseCase.execute(aCustomerId))
                .thenThrow(NotFoundException.with(Customer.class, aCustomerId).get());

        final var aOutput = orderCustomerGatewayImpl.findByCustomerId(aCustomerId);

        Assertions.assertTrue(aOutput.isEmpty());
    }
}
