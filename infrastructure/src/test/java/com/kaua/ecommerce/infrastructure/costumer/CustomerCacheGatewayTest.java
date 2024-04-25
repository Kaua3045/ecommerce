package com.kaua.ecommerce.infrastructure.costumer;

import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Cpf;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.Telephone;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.customer.CustomerCacheGateway;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@CacheGatewayTest
public class CustomerCacheGatewayTest extends CacheTestConfiguration {

    @Autowired
    private CustomerCacheGateway customerCacheGateway;

    @Autowired
    private CustomerCacheEntityRepository customerRepository;

    @Test
    void givenAValidCustomer_whenCallSave_shouldReturnACustomerSavedInCache() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        Assertions.assertEquals(0, customerRepository.count());

        customerCacheGateway.save(aCustomer);

        Assertions.assertEquals(1, customerRepository.count());

        final var actualCustomer = customerCacheGateway.get(aCustomer.getAccountId()).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertTrue(actualCustomer.getCpf().isEmpty());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualCustomer.getUpdatedAt());

        final var actualEntity = customerRepository.findById(actualCustomer.getAccountId()).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualEntity.getCustomerId());
        Assertions.assertEquals(aCustomer.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualEntity.getEmail());
        Assertions.assertTrue(actualCustomer.getCpf().isEmpty());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidCustomer_whenCallSave_shouldReturnACustomerUpdated() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aCleanCpf = Cpf.newCpf("50212367099");
        final var aTelephone = Telephone.newTelephone("+11234567890");

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerCacheEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerWithCpf = aCustomer.changeCpf(aCleanCpf);
        final var aCustomerWithTelephoneAndCpf = aCustomerWithCpf.changeTelephone(aTelephone);
        customerCacheGateway.save(aCustomerWithTelephoneAndCpf);

        final var actualCustomer = customerCacheGateway.get(aCustomer.getAccountId()).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertEquals(aCleanCpf.getValue(), actualCustomer.getCpf().get().getValue());
        Assertions.assertEquals(aTelephone.getValue(), actualCustomer.getTelephone().get().getValue());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(actualCustomer.getUpdatedAt()));

        final var actualEntity = customerRepository.findById(actualCustomer.getAccountId()).get();

        Assertions.assertEquals(aCustomerWithCpf.getId().getValue(), actualEntity.getCustomerId());
        Assertions.assertEquals(aCustomerWithCpf.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomerWithCpf.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomerWithCpf.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomerWithCpf.getEmail(), actualEntity.getEmail());
        Assertions.assertEquals(aCustomerWithCpf.getCpf().get().getValue(), actualEntity.getCpf());
        Assertions.assertEquals(aCustomerWithCpf.getTelephone().get().getValue(), actualEntity.getTelephone());
        Assertions.assertEquals(aCustomerWithCpf.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCustomerWithCpf.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidAccountId_whenCallFindById_shouldReturnACustomer() {
        final var aCustomer = Fixture.Customers.customerWithCpf;
        final var aAccountId = aCustomer.getAccountId();
        final var aCleanCpf = aCustomer.getCpf().get().getValue();

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerCacheEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        final var actualCustomer = customerCacheGateway.get(aAccountId).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertEquals(aCleanCpf, actualCustomer.getCpf().get().getValue());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualCustomer.getUpdatedAt());

        final var actualEntity = customerRepository.findById(actualCustomer.getAccountId()).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualEntity.getCustomerId());
        Assertions.assertEquals(aCustomer.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualEntity.getEmail());
        Assertions.assertEquals(aCustomer.getCpf().get().getValue(), actualEntity.getCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidAccountIdButNotStored_whenCallGetInCache_shouldReturnEmpty() {
        final var aAccountId = "123";

        Assertions.assertEquals(0, customerRepository.count());

        final var actualCustomer = customerCacheGateway.get(aAccountId);

        Assertions.assertTrue(actualCustomer.isEmpty());
    }

    @Test
    void givenAValidAccountId_whenCallDelete_shouldBeOk() {
        final var aCustomer = Fixture.Customers.customerDefault;

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerCacheEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        customerCacheGateway.delete(aCustomer.getAccountId());

        Assertions.assertEquals(0, customerRepository.count());
    }

    @Test
    void givenAnInvalidAccountId_whenCallDelete_shouldBeOk() {
        final var aAccountId = "123";

        Assertions.assertEquals(0, customerRepository.count());

        customerCacheGateway.delete(aAccountId);

        Assertions.assertEquals(0, customerRepository.count());
    }
}
