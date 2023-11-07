package com.kaua.ecommerce.application.customer.update.cpf;

import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfCommand;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@CacheGatewayTest
public class UpdateCustomerCpfUseCaseIT extends CacheTestConfiguration {

    @Autowired
    private UpdateCustomerCpfUseCase updateCustomerCpfUseCase;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private CustomerCacheRepository customerCacheRepository;

    @Test
    void givenAValidValues_whenCallsUpdateCustomerCpf_shouldReturnAccountId() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aCpf = "815.959.150-01";
        final var aCpfCleaned = "81595915001";

        this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        final var aResult = this.updateCustomerCpfUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.accountId());

        final var aCustomerUpdated = this.customerRepository.findByAccountId(aAccountId).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), aCustomerUpdated.getId());
        Assertions.assertEquals(aAccountId, aCustomerUpdated.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), aCustomerUpdated.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), aCustomerUpdated.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), aCustomerUpdated.getEmail());
        Assertions.assertEquals(aCpfCleaned, aCustomerUpdated.getCpf());
        Assertions.assertNull(aCustomerUpdated.getTelephone());
        Assertions.assertTrue(aCustomerUpdated.getAddress().isEmpty());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aCustomerUpdated.getCreatedAt());
        Assertions.assertTrue(aCustomer.getUpdatedAt().isBefore(aCustomerUpdated.getUpdatedAt()));
        Assertions.assertEquals(1, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
    }

    @Test
    void givenAnInvalidAccountId_whenCallsUpdateCustomerCpf_shouldThrowNotFoundException() {
        final var aAccountId = IdUtils.generate();
        final var aCpf = "815.959.150-01";

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateCustomerCpfUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());
        Assertions.assertEquals(0, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
    }

    @Test
    void givenAnInvalidCpf_whenCallsUpdateCustomerCpf_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aCpf = "01010101010";

        this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        final var expectedErrorMessage = "'cpf' invalid";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        final var aResult = Assertions.assertThrows(DomainException.class,
                () -> this.updateCustomerCpfUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
    }
}
