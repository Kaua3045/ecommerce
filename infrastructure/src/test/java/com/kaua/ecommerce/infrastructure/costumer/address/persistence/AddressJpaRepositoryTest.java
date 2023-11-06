package com.kaua.ecommerce.infrastructure.costumer.address.persistence;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaRepository;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@IntegrationTest
public class AddressJpaRepositoryTest {

    @Autowired
    private AddressJpaRepository addressRepository;

    @Test
    void givenAnInvalidNullStreet_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "street";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity.street";

        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setStreet(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> addressRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullNumber_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "number";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity.number";

        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setNumber(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> addressRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullZipCode_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "zipCode";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity.zipCode";

        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setZipCode(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> addressRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCustomerId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "customerId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity.customerId";

        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setCustomerId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> addressRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity";

        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> addressRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidComplement_whenCallSave_shouldReturnAddress() {
        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setComplement("Ap 01");

        final var actualResult = Assertions.assertDoesNotThrow(() -> addressRepository.save(aEntity));

        Assertions.assertEquals(aEntity.getId(), actualResult.getId());
        Assertions.assertEquals(aEntity.getStreet(), actualResult.getStreet());
        Assertions.assertEquals(aEntity.getNumber(), actualResult.getNumber());
        Assertions.assertEquals(aEntity.getComplement(), actualResult.getComplement());
        Assertions.assertEquals(aEntity.getZipCode(), actualResult.getZipCode());
        Assertions.assertEquals(aEntity.getCustomerId(), aAddress.getCustomerID().getValue());
    }

    @Test
    void givenAValidNullComplement_whenCallSave_shouldReturnAddress() {
        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setComplement(null);

        final var actualResult = Assertions.assertDoesNotThrow(() -> addressRepository.save(aEntity));

        Assertions.assertEquals(aEntity.getId(), actualResult.getId());
        Assertions.assertEquals(aEntity.getStreet(), actualResult.getStreet());
        Assertions.assertEquals(aEntity.getNumber(), actualResult.getNumber());
        Assertions.assertNull(actualResult.getComplement());
        Assertions.assertEquals(aEntity.getZipCode(), actualResult.getZipCode());
        Assertions.assertEquals(aEntity.getCustomerId(), aAddress.getCustomerID().getValue());
    }

    @Test
    void givenAnInvalidNullDistrict_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "district";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity.district";

        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setDistrict(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> addressRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCity_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "city";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity.city";

        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setCity(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> addressRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullState_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "state";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity.state";

        final var aAddress = Fixture.Addresses.addressDefault;

        final var aEntity = AddressJpaEntity.toEntity(aAddress);
        aEntity.setState(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> addressRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
