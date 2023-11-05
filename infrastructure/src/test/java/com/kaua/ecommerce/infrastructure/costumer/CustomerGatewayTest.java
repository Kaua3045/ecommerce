package com.kaua.ecommerce.infrastructure.costumer;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Cpf;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.Telephone;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.customer.CustomerMySQLGateway;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CustomerGatewayTest {

    @Autowired
    private CustomerMySQLGateway customerGateway;

    @Autowired
    private CustomerJpaRepository customerRepository;

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
        Assertions.assertTrue(actualCustomer.getCpf().isEmpty());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualCustomer.getUpdatedAt());

        final var actualEntity = customerRepository.findById(actualCustomer.getId().getValue()).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCustomer.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualEntity.getEmail());
        Assertions.assertTrue(actualCustomer.getCpf().isEmpty());
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
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        Assertions.assertEquals(0, customerRepository.count());

        customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        Assertions.assertEquals(1, customerRepository.count());

        Assertions.assertTrue(customerGateway.existsByAccountId(aAccountId));
    }

    @Test
    void givenAValidCustomer_whenCallUpdate_shouldReturnACustomerUpdated() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aCleanCpf = Cpf.newCpf("50212367099");
        final var aTelephone = Telephone.newTelephone("+11234567890");

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerWithCpf = aCustomer.changeCpf(aCleanCpf);
        final var aCustomerWithTelephoneAndCpf = aCustomerWithCpf.changeTelephone(aTelephone);
        final var actualCustomer = customerGateway.update(aCustomerWithTelephoneAndCpf);

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertEquals(aCleanCpf.getValue(), actualCustomer.getCpf().get().getValue());
        Assertions.assertEquals(aTelephone.getValue(), actualCustomer.getTelephone().get().getValue());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(actualCustomer.getUpdatedAt()));

        final var actualEntity = customerRepository.findById(actualCustomer.getId().getValue()).get();

        Assertions.assertEquals(aCustomerWithCpf.getId().getValue(), actualEntity.getId());
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
    void givenAValidCustomerWithAddress_whenCallUpdate_shouldReturnACustomerUpdated() {
        final var aCustomer = Fixture.Customers.customerWithTelephoneAndCpf;
        final var aAddress = Fixture.Addresses.addressDefault;

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerWithAddress = aCustomer.changeAddress(aAddress);
        final var actualCustomer = customerGateway.update(aCustomerWithAddress);

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertEquals(aCustomer.getCpf().get().getValue(), actualCustomer.getCpf().get().getValue());
        Assertions.assertEquals(aCustomer.getTelephone().get().getValue(), actualCustomer.getTelephone().get().getValue());
        Assertions.assertEquals(aAddress.getStreet(), actualCustomer.getAddress().get().getStreet());
        Assertions.assertEquals(aAddress.getNumber(), actualCustomer.getAddress().get().getNumber());
        Assertions.assertEquals(aAddress.getComplement(), actualCustomer.getAddress().get().getComplement());
        Assertions.assertEquals(aAddress.getDistrict(), actualCustomer.getAddress().get().getDistrict());
        Assertions.assertEquals(aAddress.getCity(), actualCustomer.getAddress().get().getCity());
        Assertions.assertEquals(aAddress.getState(), actualCustomer.getAddress().get().getState());
        Assertions.assertEquals(aAddress.getZipCode(), actualCustomer.getAddress().get().getZipCode());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(actualCustomer.getUpdatedAt()));

        final var actualEntity = customerRepository.findById(actualCustomer.getId().getValue()).get();

        Assertions.assertEquals(aCustomerWithAddress.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCustomerWithAddress.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomerWithAddress.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomerWithAddress.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomerWithAddress.getEmail(), actualEntity.getEmail());
        Assertions.assertEquals(aCustomerWithAddress.getCpf().get().getValue(), actualEntity.getCpf());
        Assertions.assertEquals(aCustomerWithAddress.getTelephone().get().getValue(), actualEntity.getTelephone());
        Assertions.assertEquals(aCustomerWithAddress.getAddress().get().getStreet(), actualEntity.getAddress().get().getStreet());
        Assertions.assertEquals(aCustomerWithAddress.getAddress().get().getNumber(), actualEntity.getAddress().get().getNumber());
        Assertions.assertEquals(aCustomerWithAddress.getAddress().get().getComplement(), actualEntity.getAddress().get().getComplement());
        Assertions.assertEquals(aCustomerWithAddress.getAddress().get().getDistrict(), actualEntity.getAddress().get().getDistrict());
        Assertions.assertEquals(aCustomerWithAddress.getAddress().get().getCity(), actualEntity.getAddress().get().getCity());
        Assertions.assertEquals(aCustomerWithAddress.getAddress().get().getState(), actualEntity.getAddress().get().getState());
        Assertions.assertEquals(aCustomerWithAddress.getAddress().get().getZipCode(), actualEntity.getAddress().get().getZipCode());
        Assertions.assertEquals(aCustomerWithAddress.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCustomerWithAddress.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidAccountId_whenCallFindByAccountId_shouldReturnACustomer() {
        final var aCustomer = Fixture.Customers.customerWithCpf;
        final var aAccountId = aCustomer.getAccountId();
        final var aCleanCpf = aCustomer.getCpf().get().getValue();

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        final var actualCustomer = customerGateway.findByAccountId(aAccountId).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualCustomer.getId().getValue());
        Assertions.assertEquals(aCustomer.getAccountId(), actualCustomer.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualCustomer.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualCustomer.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualCustomer.getEmail());
        Assertions.assertEquals(aCleanCpf, actualCustomer.getCpf().get().getValue());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualCustomer.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualCustomer.getUpdatedAt());

        final var actualEntity = customerRepository.findById(actualCustomer.getId().getValue()).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCustomer.getAccountId(), actualEntity.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), actualEntity.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), actualEntity.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), actualEntity.getEmail());
        Assertions.assertEquals(aCustomer.getCpf().get().getValue(), actualEntity.getCpf());
        Assertions.assertEquals(aCustomer.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidAccountId_whenCallDeleteById_shouldBeOk() {
        final var aCustomer = Fixture.Customers.customerDefault;

        Assertions.assertEquals(0, customerRepository.count());
        customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));
        Assertions.assertEquals(1, customerRepository.count());

        customerGateway.deleteById(aCustomer.getAccountId());

        Assertions.assertEquals(0, customerRepository.count());
    }

    @Test
    void givenAnInvalidAccountId_whenCallDeleteById_shouldBeOk() {
        final var aAccountId = "123";

        Assertions.assertEquals(0, customerRepository.count());

        customerGateway.deleteById(aAccountId);

        Assertions.assertEquals(0, customerRepository.count());
    }
}
