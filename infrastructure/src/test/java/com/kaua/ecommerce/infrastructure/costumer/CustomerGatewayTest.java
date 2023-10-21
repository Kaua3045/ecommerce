package com.kaua.ecommerce.infrastructure.costumer;

import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.customer.CustomerMySQLGateway;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerjpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CustomerGatewayTest {

    @Autowired
    private CustomerMySQLGateway customerGateway;

    @Autowired
    private CustomerjpaRepository customerRepository;

    @Test
    void givenAValidCustomer_whenCallCreate_shouldReturnACustomerCreated() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        Assertions.assertEquals(0, customerRepository.count());

        final var actualCustomer = customerGateway.create(aCustomer);

        Assertions.assertEquals(1, customerRepository.count());

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertNull(actualCustomer.getCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualCustomer.getUpdatedAt());

        final var actualEntity = customerRepository.findById(actualCustomer.getId().getValue()).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCustomer.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualEntity.getEmail());
        Assertions.assertNull(actualCustomer.getCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidAccountIdButNotExists_whenCallExistsByAccountId_shouldReturnFalse() {
        final var aAccountId = "123456789";

        Assertions.assertEquals(0, customerRepository.count());
        Assertions.assertFalse(customerGateway.existsByAccountId(aAccountId));
    }

    @Test
    void givenAValidAccountId_whenCallExistsByAccountId_shouldReturnTrue() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        Assertions.assertEquals(0, customerRepository.count());

        customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        Assertions.assertEquals(1, customerRepository.count());

        Assertions.assertTrue(customerGateway.existsByAccountId(aAccountId));
    }

    @Test
    void givenAValidCustomer_whenCallUpdate_shouldReturnACustomerUpdated() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";
        final var aCleanCpf = "50212367099";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerWithCpf = aCustomer.changeCpf(aCleanCpf);
        final var actualCustomer = customerGateway.update(aCustomerWithCpf);

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertEquals(aCleanCpf, actualCustomer.getCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(actualCustomer.getUpdatedAt()));

        final var actualEntity = customerRepository.findById(actualCustomer.getId().getValue()).get();

        Assertions.assertEquals(aCustomerWithCpf.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCustomerWithCpf.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomerWithCpf.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomerWithCpf.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomerWithCpf.getEmail(), actualEntity.getEmail());
        Assertions.assertEquals(aCustomerWithCpf.getCpf(), actualEntity.getCpf());
        Assertions.assertEquals(aCustomerWithCpf.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCustomerWithCpf.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidAccountId_whenCallFindByAccountId_shouldReturnACustomer() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";
        final var aCleanCpf = "50212367099";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail)
                .changeCpf(aCleanCpf);

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        final var actualCustomer = customerGateway.findByAccountId(aAccountId).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertEquals(aCleanCpf, actualCustomer.getCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualCustomer.getUpdatedAt());

        final var actualEntity = customerRepository.findById(actualCustomer.getId().getValue()).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCustomer.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualEntity.getEmail());
        Assertions.assertEquals(aCustomer.getCpf(), actualEntity.getCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualEntity.getUpdatedAt());
    }
}
