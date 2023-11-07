package com.kaua.ecommerce.application.customer.update.telephone;

import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneCommand;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
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
public class UpdateCustomerTelephoneUseCaseIT extends CacheTestConfiguration {

    @Autowired
    private UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private CustomerCacheRepository customerCacheRepository;

    @Test
    void givenAValidValues_whenCallsUpdateCustomerTelephone_shouldReturnAccountId() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aTelephone = "+552724458092";

        this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        final var aResult = this.updateCustomerTelephoneUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.accountId());

        final var aCustomerUpdated = this.customerRepository.findByAccountId(aAccountId).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), aCustomerUpdated.getId());
        Assertions.assertEquals(aAccountId, aCustomerUpdated.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), aCustomerUpdated.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), aCustomerUpdated.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), aCustomerUpdated.getEmail());
        Assertions.assertNull(aCustomerUpdated.getCpf());
        Assertions.assertEquals(aTelephone, aCustomerUpdated.getTelephone());
        Assertions.assertTrue(aCustomerUpdated.getAddress().isEmpty());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aCustomerUpdated.getCreatedAt());
        Assertions.assertTrue(aCustomer.getUpdatedAt().isBefore(aCustomerUpdated.getUpdatedAt()));
        Assertions.assertEquals(1, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
    }

    @Test
    void givenAnInvalidAccountId_whenCallsUpdateCustomerTelephone_shouldThrowNotFoundException() {
        final var aAccountId = IdUtils.generate();
        final var aTelephone = "+552724458092";

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateCustomerTelephoneUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());
        Assertions.assertEquals(0, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
    }

    @Test
    void givenAnInvalidTelephone_whenCallsUpdateCustomerTelephone_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aTelephone = "+551199999999";

        this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        final var expectedErrorMessage = "'telephone' invalid";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        final var aResult = this.updateCustomerTelephoneUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
    }
}
