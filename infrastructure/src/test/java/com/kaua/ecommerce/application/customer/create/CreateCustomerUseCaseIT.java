package com.kaua.ecommerce.application.customer.create;

import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerCommand;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerUseCase;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CreateCustomerUseCaseIT {

    @Autowired
    private CreateCustomerUseCase createCustomerUseCase;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Test
    void givenAValidValues_whenCallsCreateCustomerUseCase_thenCustomerShouldBeCreated() {
        final var aAccountId = IdUtils.generate();
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tess.com";

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        final var aResult = this.createCustomerUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCustomer = this.customerRepository.findByAccountId(aAccountId).get();

        Assertions.assertNotNull(aCustomer.getId());
        Assertions.assertEquals(aAccountId, aCustomer.getAccountId());
        Assertions.assertEquals(aFirstName, aCustomer.getFirstName());
        Assertions.assertEquals(aLastName, aCustomer.getLastName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertNull(aCustomer.getCpf());
        Assertions.assertNull(aCustomer.getTelephone());
        Assertions.assertTrue(aCustomer.getAddress().isEmpty());
        Assertions.assertNotNull(aCustomer.getCreatedAt());
        Assertions.assertNotNull(aCustomer.getUpdatedAt());
        Assertions.assertEquals(1, this.customerRepository.count());
    }

    @Test
    void givenAnInvalidAccountIdExists_whenCallsCreateCustomerUseCase_shouldReturnDomainException() {
        final var aAccountId = IdUtils.generate();

        this.customerRepository.save(CustomerJpaEntity.toEntity(Customer.newCustomer(
                aAccountId,
                "Man",
                "For",
                "man.for@test.com")));

        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tess.com";

        final var expectedErrorMessage = "Customer already exists with this account id";
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        final var aResult = this.createCustomerUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.customerRepository.count());
    }

    @Test
    void givenAnInvalidFirstName_whenCallsCreateCustomerUseCase_shouldReturnDomainException() {
        final var aAccountId = IdUtils.generate();
        final String aFirstName = null;
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@tess.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("firstName");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        final var aResult = this.createCustomerUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.customerRepository.count());
    }

    @Test
    void givenAnInvalidLastName_whenCallsCreateCustomerUseCase_shouldReturnDomainException() {
        final var aAccountId = IdUtils.generate();
        final var aFirstName = "Teste";
        final String aLastName = null;
        final var aEmail = "teste.testes@tess.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("lastName");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        final var aResult = this.createCustomerUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.customerRepository.count());
    }

    @Test
    void givenAnInvalidEmail_whenCallsCreateCustomerUseCase_shouldReturnDomainException() {
        final var aAccountId = IdUtils.generate();
        final var aFirstName = "Testes";
        final var aLastName = "Testes";
        final String aEmail = null;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("email");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        final var aResult = this.createCustomerUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.customerRepository.count());
    }
}
