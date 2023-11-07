package com.kaua.ecommerce.application.customer.delete;

import com.kaua.ecommerce.application.usecases.customer.delete.DeleteCustomerUseCase;
import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@CacheGatewayTest
public class DeleteCustomerUseCaseIT extends CacheTestConfiguration {

    @Autowired
    private DeleteCustomerUseCase deleteCustomerUseCase;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private CustomerCacheRepository customerCacheRepository;

    @Autowired
    private AddressJpaRepository addressRepository;

    @Test
    void givenAValidAccountId_whenCallsDeleteCustomerUseCase_shouldBeOk() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aAccountId = aCustomer.getAccountId();

        this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));
        this.customerCacheRepository.save(CustomerCacheEntity.toEntity(aCustomer));

        Assertions.assertDoesNotThrow(() -> this.deleteCustomerUseCase.execute(aAccountId));

        Assertions.assertEquals(0, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
        Assertions.assertEquals(0, this.addressRepository.count());
    }

    @Test
    void givenAnInvalidAccountId_whenCallsDeleteCustomerUseCase_shouldBeOk() {
        final var aAccountId = "empty";

        Assertions.assertDoesNotThrow(() -> this.deleteCustomerUseCase.execute(aAccountId));

        Assertions.assertEquals(0, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
        Assertions.assertEquals(0, this.addressRepository.count());
    }
}
