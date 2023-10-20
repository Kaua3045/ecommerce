package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
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

    @Test
    void givenAValidValues_whenCallWith_shouldReturnCustomerObject() {
        final var aId = "123456789";
        final var aAccountId = "10102012012010";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tessss.com";
        final var aCpf = "12345678901";
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aCustomer = Customer.with(
                aId,
                aAccountId,
                aFirstName,
                aLastName,
                aEmail,
                aCpf,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aId, aCustomer.getId().getValue());
        Assertions.assertEquals(aAccountId, aCustomer.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomer.getFirstName());
        Assertions.assertEquals(aLastName, aCustomer.getLastName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertEquals(aCpf, aCustomer.getCpf());
        Assertions.assertEquals(aCreatedAt, aCustomer.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aCustomer.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aCustomer.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidCpf_whenCallChangeCpf_shouldReturnACustomerWithCpf() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";
        final var aCpf = "502.123.670-99";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);
        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerWithCpf = aCustomer.changeCpf(aCpf);

        Assertions.assertNotNull(aCustomerWithCpf);
        Assertions.assertEquals(aCustomerWithCpf.getId(), aCustomer.getId());
        Assertions.assertEquals(aAccountId, aCustomerWithCpf.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomerWithCpf.getFirstName());
        Assertions.assertEquals(aLastName, aCustomerWithCpf.getLastName());
        Assertions.assertEquals(aEmail, aCustomerWithCpf.getEmail());
        Assertions.assertEquals(aCpf, aCustomerWithCpf.getCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aCustomerWithCpf.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(aCustomerWithCpf.getUpdatedAt()));

        Assertions.assertDoesNotThrow(() -> aCustomerWithCpf.validate(new ThrowsValidationHandler()));
    }
}
