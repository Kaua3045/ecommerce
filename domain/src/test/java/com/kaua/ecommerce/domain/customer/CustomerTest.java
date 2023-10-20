package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
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

    @Test
    void givenAInvalidNullAccountId_whenCallNewCustomer_shouldReturnDomainException() {
        final String aAccountId = null;
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("accountId");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertTrue(aTestValidationHandler.hasError());
    }

    @Test
    void givenAInvalidBlankAccountId_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = " ";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("accountId");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNullFirstName_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final String aFirstName = null;
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("firstName");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidBlankFirstName_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = " ";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("firstName");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidFirstNameLengthLessThan3_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Te ";
        final var aLastName = "Testes";
        final var aEmail = "teste@testes.com";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("firstName", 3, 255);
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidFirstNameLengthMoreThan255_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = RandomStringUtils.generateValue(256);
        final var aLastName = "Testes";
        final var aEmail = "teste@testes.com";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("firstName", 3, 255);
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNullLastName_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "teste";
        final String aLastName = null;
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("lastName");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidBlankLastName_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "teste";
        final var aLastName = " ";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("lastName");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidLastNameLengthLessThan3_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Testes";
        final var aLastName = "Te ";
        final var aEmail = "teste@testes.com";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("lastName", 3, 255);
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidLastNameLengthMoreThan255_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "teste";
        final var aLastName = RandomStringUtils.generateValue(256);
        final var aEmail = "teste@testes.com";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("lastName", 3, 255);
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNullEmail_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "teste";
        final var aLastName = "Testes";
        final String aEmail = null;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("email");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidBlankEmail_whenCallNewCustomer_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "teste";
        final var aLastName = "Testes";
        final var aEmail = " ";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("email");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aTestValidationHandler = new TestValidationHandler();
        aCustomer.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }
}
