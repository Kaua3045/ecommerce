package com.kaua.ecommerce.infrastructure.order.persistence;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@DatabaseGatewayTest
public class OrderDeliveryJpaEntityRepositoryTest {

    @Autowired
    private OrderDeliveryJpaEntityRepository orderDeliveryJpaEntityRepository;

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity";

        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> orderDeliveryJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullFreightType_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "freightType";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity.freightType";

        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setFreightType(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderDeliveryJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullStreet_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "street";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity.street";

        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setStreet(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderDeliveryJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullNumber_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "number";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity.number";

        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setNumber(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderDeliveryJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCity_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "city";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity.city";

        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setCity(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderDeliveryJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullState_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "state";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity.state";

        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setState(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderDeliveryJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullZipCode_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "zipCode";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity.zipCode";

        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setZipCode(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderDeliveryJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidZeroFreightPrice_whenCallSave_shouldReturnOrderDelivery() {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setFreightPrice(0.0f);

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderDeliveryJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getFreightType(), actualResult.getFreightType());
        Assertions.assertEquals(aEntity.getStreet(), actualResult.getStreet());
        Assertions.assertEquals(aEntity.getNumber(), actualResult.getNumber());
        Assertions.assertEquals(aEntity.getCity(), actualResult.getCity());
        Assertions.assertEquals(aEntity.getState(), actualResult.getState());
        Assertions.assertEquals(aEntity.getZipCode(), actualResult.getZipCode());
        Assertions.assertEquals(aEntity.getFreightPrice(), actualResult.getFreightPrice());
    }

    @Test
    void givenAValidOrderDelivery_whenCallSaveAndToDomain_shouldReturnOrderDelivery() {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderDeliveryJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getFreightType(), actualResult.getFreightType());
        Assertions.assertEquals(aEntity.getStreet(), actualResult.getStreet());
        Assertions.assertEquals(aEntity.getNumber(), actualResult.getNumber());
        Assertions.assertEquals(aEntity.getCity(), actualResult.getCity());
        Assertions.assertEquals(aEntity.getState(), actualResult.getState());
        Assertions.assertEquals(aEntity.getZipCode(), actualResult.getZipCode());
        Assertions.assertEquals(aEntity.getFreightPrice(), actualResult.getFreightPrice());
    }

    @Test
    void givenAValidDeliveryEstimated_whenCallSave_shouldReturnOrderDelivery() {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setDeliveryEstimated(1);

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderDeliveryJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getFreightType(), actualResult.getFreightType());
        Assertions.assertEquals(aEntity.getStreet(), actualResult.getStreet());
        Assertions.assertEquals(aEntity.getNumber(), actualResult.getNumber());
        Assertions.assertEquals(aEntity.getCity(), actualResult.getCity());
        Assertions.assertEquals(aEntity.getState(), actualResult.getState());
        Assertions.assertEquals(aEntity.getZipCode(), actualResult.getZipCode());
        Assertions.assertEquals(aEntity.getFreightPrice(), actualResult.getFreightPrice());
        Assertions.assertEquals(aEntity.getDeliveryEstimated(), actualResult.getDeliveryEstimated());
    }

    @Test
    void givenAValidNullComplement_whenCallSave_shouldReturnOrderDelivery() {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setComplement(null);

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderDeliveryJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getFreightType(), actualResult.getFreightType());
        Assertions.assertEquals(aEntity.getStreet(), actualResult.getStreet());
        Assertions.assertEquals(aEntity.getNumber(), actualResult.getNumber());
        Assertions.assertEquals(aEntity.getCity(), actualResult.getCity());
        Assertions.assertEquals(aEntity.getState(), actualResult.getState());
        Assertions.assertEquals(aEntity.getZipCode(), actualResult.getZipCode());
        Assertions.assertEquals(aEntity.getFreightPrice(), actualResult.getFreightPrice());
        Assertions.assertEquals(aEntity.getDeliveryEstimated(), actualResult.getDeliveryEstimated());
        Assertions.assertEquals(aEntity.getComplement(), actualResult.getComplement());
    }

    @Test
    void givenAnInvalidNullDistrict_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "district";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntity.district";

        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aEntity = OrderDeliveryJpaEntity.toEntity(aOrderDelivery);
        aEntity.setDistrict(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderDeliveryJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
