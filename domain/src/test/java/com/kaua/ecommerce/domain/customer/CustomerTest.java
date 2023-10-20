package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomerTest {

    @Test
    void givenAValidValues_whenCallNewCustomer_shouldReturnACustomerCreated() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        Assertions.assertNotNull(aCustomer);
        Assertions.assertNotNull(aCustomer.getId());
        Assertions.assertEquals(aAccountId, aCustomer.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomer.getFirstName());
        Assertions.assertEquals(aLastName, aCustomer.getLastName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertNull(aCustomer.getCpf());
        Assertions.assertNotNull(aCustomer.getCreatedAt());
        Assertions.assertNotNull(aCustomer.getUpdatedAt());

        Assertions.assertDoesNotThrow(() -> aCustomer.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testCustomerIdEqualsAndHashCode() {
        final var aCustomerId = CustomerID.from("123456789");
        final var anotherCustomerId = CustomerID.from("123456789");

        Assertions.assertTrue(aCustomerId.equals(anotherCustomerId));
        Assertions.assertTrue(aCustomerId.equals(aCustomerId));
        Assertions.assertFalse(aCustomerId.equals(null));
        Assertions.assertFalse(aCustomerId.equals(""));
        Assertions.assertEquals(aCustomerId.hashCode(), anotherCustomerId.hashCode());
    }
}
