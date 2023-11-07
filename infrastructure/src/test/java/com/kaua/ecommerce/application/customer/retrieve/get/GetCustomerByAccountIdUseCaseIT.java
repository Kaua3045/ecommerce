package com.kaua.ecommerce.application.customer.retrieve.get;

import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@CacheGatewayTest
public class GetCustomerByAccountIdUseCaseIT extends CacheTestConfiguration {

    @Autowired
    private GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private CustomerCacheRepository customerCacheRepository;

    @Test
    void givenAValidAccountId_whenCallsGetCustomerByAccountIdFromDatabase_shouldReturnCustomer() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aAccountId = aCustomer.getAccountId();

        this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        final var aResult = this.getCustomerByAccountIdUseCase.execute(aAccountId);

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aCustomer.getId().getValue(), aResult.id());
        Assertions.assertEquals(aAccountId, aResult.accountId());
        Assertions.assertEquals(aCustomer.getFirstName(), aResult.firstName());
        Assertions.assertEquals(aCustomer.getLastName(), aResult.lastName());
        Assertions.assertEquals(aCustomer.getEmail(), aResult.email());
        Assertions.assertEquals(aCustomer.getCpf().get().getFormattedCpf(), aResult.cpf());
        Assertions.assertEquals(aCustomer.getTelephone().get().getValue(), aResult.telephone());
        Assertions.assertEquals(aCustomer.getAddress().get().getStreet(), aResult.address().street());
        Assertions.assertEquals(aCustomer.getAddress().get().getNumber(), aResult.address().number());
        Assertions.assertEquals(aCustomer.getAddress().get().getComplement(), aResult.address().complement());
        Assertions.assertEquals(aCustomer.getAddress().get().getDistrict(), aResult.address().district());
        Assertions.assertEquals(aCustomer.getAddress().get().getCity(), aResult.address().city());
        Assertions.assertEquals(aCustomer.getAddress().get().getState(), aResult.address().state());
        Assertions.assertEquals(aCustomer.getAddress().get().getZipCode(), aResult.address().zipCode());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aResult.createdAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), aResult.updatedAt());
        Assertions.assertEquals(1, this.customerRepository.count());
        Assertions.assertEquals(1, this.customerCacheRepository.count());
    }

    @Test
    void givenAValidAccountId_whenCallsGetCustomerByAccountIdFromCache_shouldReturnCustomer() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aAccountId = aCustomer.getAccountId();

        this.customerCacheRepository.save(CustomerCacheEntity.toEntity(aCustomer));

        final var aResult = this.getCustomerByAccountIdUseCase.execute(aAccountId);

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aCustomer.getId().getValue(), aResult.id());
        Assertions.assertEquals(aAccountId, aResult.accountId());
        Assertions.assertEquals(aCustomer.getFirstName(), aResult.firstName());
        Assertions.assertEquals(aCustomer.getLastName(), aResult.lastName());
        Assertions.assertEquals(aCustomer.getEmail(), aResult.email());
        Assertions.assertEquals(aCustomer.getCpf().get().getFormattedCpf(), aResult.cpf());
        Assertions.assertEquals(aCustomer.getTelephone().get().getValue(), aResult.telephone());
        Assertions.assertEquals(aCustomer.getAddress().get().getStreet(), aResult.address().street());
        Assertions.assertEquals(aCustomer.getAddress().get().getNumber(), aResult.address().number());
        Assertions.assertEquals(aCustomer.getAddress().get().getComplement(), aResult.address().complement());
        Assertions.assertEquals(aCustomer.getAddress().get().getDistrict(), aResult.address().district());
        Assertions.assertEquals(aCustomer.getAddress().get().getCity(), aResult.address().city());
        Assertions.assertEquals(aCustomer.getAddress().get().getState(), aResult.address().state());
        Assertions.assertEquals(aCustomer.getAddress().get().getZipCode(), aResult.address().zipCode());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aResult.createdAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), aResult.updatedAt());
        Assertions.assertEquals(0, this.customerRepository.count());
        Assertions.assertEquals(1, this.customerCacheRepository.count());
    }

    @Test
    void givenAnInvalidAccountId_whenCallsGetCustomerByAccountId_shouldThrowNotFoundException() {
        final var aAccountId = IdUtils.generate();

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> this.getCustomerByAccountIdUseCase.execute(aAccountId));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());
        Assertions.assertEquals(0, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
    }
}
