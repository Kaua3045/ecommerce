package com.kaua.ecommerce.infrastructure.costumer.persistence;

import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerjpaRepository;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@IntegrationTest
public class CustomerJpaRepositoryTest {

    @Autowired
    private CustomerjpaRepository customerRepository;

    @Test
    void givenAnInvalidNullAccountId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "accountId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity.accountId";

        final var aCustomer = Customer.newCustomer(
                "123",
                "teste",
                "testes",
                "teste@teste.com"
        );

        final var aEntity = CustomerJpaEntity.toEntity(aCustomer);
        aEntity.setAccountId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> customerRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullFirstName_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "firstName";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity.firstName";

        final var aCustomer = Customer.newCustomer(
                "123",
                "teste",
                "testes",
                "teste@teste.com"
        );

        final var aEntity = CustomerJpaEntity.toEntity(aCustomer);
        aEntity.setFirstName(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> customerRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullLastName_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "lastName";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity.lastName";

        final var aCustomer = Customer.newCustomer(
                "123",
                "teste",
                "testes",
                "teste@teste.com"
        );

        final var aEntity = CustomerJpaEntity.toEntity(aCustomer);
        aEntity.setLastName(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> customerRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullEmail_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "email";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity.email";

        final var aCustomer = Customer.newCustomer(
                "123",
                "teste",
                "testes",
                "teste@teste.com"
        );

        final var aEntity = CustomerJpaEntity.toEntity(aCustomer);
        aEntity.setEmail(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> customerRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity.createdAt";

        final var aCustomer = Customer.newCustomer(
                "123",
                "teste",
                "testes",
                "teste@teste.com"
        );

        final var aEntity = CustomerJpaEntity.toEntity(aCustomer);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> customerRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity.updatedAt";

        final var aCustomer = Customer.newCustomer(
                "123",
                "teste",
                "testes",
                "teste@teste.com"
        );

        final var aEntity = CustomerJpaEntity.toEntity(aCustomer);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> customerRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity";

        final var aCustomer = Customer.newCustomer(
                "123",
                "teste",
                "testes",
                "teste@teste.com"
        );

        final var aEntity = CustomerJpaEntity.toEntity(aCustomer);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> customerRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
