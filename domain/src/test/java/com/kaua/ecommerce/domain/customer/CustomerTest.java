package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.customer.address.Address;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomerTest extends UnitTest {

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
        Assertions.assertTrue(aCustomer.getCpf().isEmpty());
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
    void givenAValidValuesWithCpf_whenCallWith_shouldReturnCustomerObject() {
        final var aId = "123456789";
        final var aAccountId = "10102012012010";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tessss.com";
        final var aCpf = "50212367099";
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aCustomer = Customer.with(
                aId,
                aAccountId,
                aFirstName,
                aLastName,
                aEmail,
                aCpf,
                null,
                null,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aId, aCustomer.getId().getValue());
        Assertions.assertEquals(aAccountId, aCustomer.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomer.getFirstName());
        Assertions.assertEquals(aLastName, aCustomer.getLastName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertEquals(aCpf, aCustomer.getCpf().get().getValue());
        Assertions.assertEquals(aCreatedAt, aCustomer.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aCustomer.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aCustomer.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesWithNullCpf_whenCallWith_shouldReturnCustomerObject() {
        final var aId = "123456789";
        final var aAccountId = "10102012012010";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tessss.com";
        final String aCpf = null;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aCustomer = Customer.with(
                aId,
                aAccountId,
                aFirstName,
                aLastName,
                aEmail,
                aCpf,
                null,
                null,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aId, aCustomer.getId().getValue());
        Assertions.assertEquals(aAccountId, aCustomer.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomer.getFirstName());
        Assertions.assertEquals(aLastName, aCustomer.getLastName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertTrue(aCustomer.getCpf().isEmpty());
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

        final var aCpf = Assertions.assertDoesNotThrow(() -> Cpf.newCpf("502.123.670-99"));

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);
        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerWithCpf = aCustomer.changeCpf(aCpf);

        Assertions.assertNotNull(aCustomerWithCpf);
        Assertions.assertEquals(aCustomerWithCpf.getId(), aCustomer.getId());
        Assertions.assertEquals(aAccountId, aCustomerWithCpf.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomerWithCpf.getFirstName());
        Assertions.assertEquals(aLastName, aCustomerWithCpf.getLastName());
        Assertions.assertEquals(aEmail, aCustomerWithCpf.getEmail());
        Assertions.assertEquals(aCpf, aCustomerWithCpf.getCpf().get());
        Assertions.assertEquals(aCpf.getValue(), aCustomerWithCpf.getCpf().get().getValue());
        Assertions.assertEquals(aCpf.getFormattedCpf(), aCustomerWithCpf.getCpf().get().getFormattedCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aCustomerWithCpf.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(aCustomerWithCpf.getUpdatedAt()));

        Assertions.assertDoesNotThrow(() -> aCustomerWithCpf.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAnInvalidCpf_whenCallNewCpf_shouldThrowsDomainException() {
        final var expectedErrorMessage = "'cpf' invalid";
        final var expectedErrorCount = 1;

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Cpf.newCpf("502.123.670-10"));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aException.getErrors().size());
    }

    @Test
    void givenAValidValuesWithTelephone_whenCallWith_shouldReturnCustomerObject() {
        final var aId = "123456789";
        final var aAccountId = "10102012012010";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tessss.com";
        final var aCpf = "50212367099";
        final var aTelephone = "+11234567890";
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aCustomer = Customer.with(
                aId,
                aAccountId,
                aFirstName,
                aLastName,
                aEmail,
                aCpf,
                aTelephone,
                null,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aId, aCustomer.getId().getValue());
        Assertions.assertEquals(aAccountId, aCustomer.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomer.getFirstName());
        Assertions.assertEquals(aLastName, aCustomer.getLastName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertEquals(aCpf, aCustomer.getCpf().get().getValue());
        Assertions.assertEquals(aTelephone, aCustomer.getTelephone().get().getValue());
        Assertions.assertEquals(aCreatedAt, aCustomer.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aCustomer.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aCustomer.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesWithNullTelephone_whenCallWith_shouldReturnCustomerObject() {
        final var aId = "123456789";
        final var aAccountId = "10102012012010";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tessss.com";
        final var aCpf = "50212367099";
        final String aTelephone = null;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aCustomer = Customer.with(
                aId,
                aAccountId,
                aFirstName,
                aLastName,
                aEmail,
                aCpf,
                aTelephone,
                null,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aId, aCustomer.getId().getValue());
        Assertions.assertEquals(aAccountId, aCustomer.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomer.getFirstName());
        Assertions.assertEquals(aLastName, aCustomer.getLastName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertEquals(aCpf, aCustomer.getCpf().get().getValue());
        Assertions.assertTrue(aCustomer.getTelephone().isEmpty());
        Assertions.assertEquals(aCreatedAt, aCustomer.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aCustomer.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aCustomer.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidTelephone_whenCallChangeTelephone_shouldReturnACustomerWithTelephone() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aTelephone = Assertions.assertDoesNotThrow(() -> Telephone.newTelephone("+11234567890"));

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);
        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerWithTelephone = aCustomer.changeTelephone(aTelephone);

        Assertions.assertNotNull(aCustomerWithTelephone);
        Assertions.assertEquals(aCustomerWithTelephone.getId(), aCustomer.getId());
        Assertions.assertEquals(aAccountId, aCustomerWithTelephone.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomerWithTelephone.getFirstName());
        Assertions.assertEquals(aLastName, aCustomerWithTelephone.getLastName());
        Assertions.assertEquals(aEmail, aCustomerWithTelephone.getEmail());
        Assertions.assertTrue(aCustomerWithTelephone.getCpf().isEmpty());
        Assertions.assertEquals(aTelephone, aCustomerWithTelephone.getTelephone().get());
        Assertions.assertEquals(aTelephone.getValue(), aCustomerWithTelephone.getTelephone().get().getValue());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aCustomerWithTelephone.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(aCustomerWithTelephone.getUpdatedAt()));

        Assertions.assertDoesNotThrow(() -> aCustomerWithTelephone.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAnInvalidNullTelephone_whenCallNewTelephone_shouldThrowsDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("telephone");
        final var expectedErrorCount = 1;

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Telephone.newTelephone(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aException.getErrors().size());
    }

    @Test
    void givenAnInvalidBlankTelephone_whenCallNewTelephone_shouldThrowsDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("telephone");
        final var expectedErrorCount = 1;

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Telephone.newTelephone(" "));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aException.getErrors().size());
    }

    @Test
    void givenAValidAddress_whenCallChangeAddress_shouldReturnACustomerWithAddress() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aAddress = Assertions.assertDoesNotThrow(() -> Address.newAddress(
                "Rua Teste",
                "123",
                "Apto 123",
                "Bairro",
                "city",
                "state",
                "12345678", CustomerID.unique()
        ));

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);
        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerWithAddress = aCustomer.changeAddress(aAddress);

        Assertions.assertNotNull(aCustomerWithAddress);
        Assertions.assertEquals(aCustomerWithAddress.getId(), aCustomer.getId());
        Assertions.assertEquals(aAccountId, aCustomerWithAddress.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomerWithAddress.getFirstName());
        Assertions.assertEquals(aLastName, aCustomerWithAddress.getLastName());
        Assertions.assertEquals(aEmail, aCustomerWithAddress.getEmail());
        Assertions.assertTrue(aCustomerWithAddress.getCpf().isEmpty());
        Assertions.assertTrue(aCustomerWithAddress.getTelephone().isEmpty());

        Assertions.assertEquals(aAddress.getStreet(), aCustomerWithAddress.getAddress().get().getStreet());
        Assertions.assertEquals(aAddress.getNumber(), aCustomerWithAddress.getAddress().get().getNumber());
        Assertions.assertEquals(aAddress.getComplement(), aCustomerWithAddress.getAddress().get().getComplement());
        Assertions.assertEquals(aAddress.getDistrict(), aCustomerWithAddress.getAddress().get().getDistrict());
        Assertions.assertEquals(aAddress.getCity(), aCustomerWithAddress.getAddress().get().getCity());
        Assertions.assertEquals(aAddress.getState(), aCustomerWithAddress.getAddress().get().getState());
        Assertions.assertEquals(aAddress.getZipCode(), aCustomerWithAddress.getAddress().get().getZipCode());

        Assertions.assertEquals(aCustomer.getCreatedAt(), aCustomerWithAddress.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(aCustomerWithAddress.getUpdatedAt()));

        Assertions.assertDoesNotThrow(() -> aCustomerWithAddress.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesWithAddress_whenCallWith_shouldReturnCustomerObject() {
        final var aId = "123456789";
        final var aAccountId = "10102012012010";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tessss.com";
        final var aCpf = "50212367099";
        final var aTelephone = "+11234567890";
        final var aAddress = Address.newAddress(
                "Rua Teste",
                "123",
                "Apto 123",
                "Bairro",
                "city",
                "state",
                "12345678", CustomerID.unique()
        );
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aCustomer = Customer.with(
                aId,
                aAccountId,
                aFirstName,
                aLastName,
                aEmail,
                aCpf,
                aTelephone,
                aAddress,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aId, aCustomer.getId().getValue());
        Assertions.assertEquals(aAccountId, aCustomer.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomer.getFirstName());
        Assertions.assertEquals(aLastName, aCustomer.getLastName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertEquals(aCpf, aCustomer.getCpf().get().getValue());
        Assertions.assertEquals(aTelephone, aCustomer.getTelephone().get().getValue());
        Assertions.assertEquals(aCreatedAt, aCustomer.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aCustomer.getUpdatedAt());

        Assertions.assertEquals(aAddress.getStreet(), aCustomer.getAddress().get().getStreet());
        Assertions.assertEquals(aAddress.getNumber(), aCustomer.getAddress().get().getNumber());
        Assertions.assertEquals(aAddress.getComplement(), aCustomer.getAddress().get().getComplement());
        Assertions.assertEquals(aAddress.getDistrict(), aCustomer.getAddress().get().getDistrict());
        Assertions.assertEquals(aAddress.getCity(), aCustomer.getAddress().get().getCity());
        Assertions.assertEquals(aAddress.getState(), aCustomer.getAddress().get().getState());
        Assertions.assertEquals(aAddress.getZipCode(), aCustomer.getAddress().get().getZipCode());

        Assertions.assertDoesNotThrow(() -> aCustomer.validate(new ThrowsValidationHandler()));
    }
}
