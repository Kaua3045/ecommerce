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

import java.util.HashSet;

@DatabaseGatewayTest
public class OrderJpaEntityRepositoryTest {

    @Autowired
    private OrderJpaEntityRepository orderJpaEntityRepository;

    @Autowired
    private OrderItemJpaEntityRepository orderItemJpaEntityRepository;

    @Test
    void givenAnInvalidNullOrderCode_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "orderCode";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity.orderCode";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setOrderCode(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity.createdAt";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity.updatedAt";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCustomerId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "customerId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity.customerId";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setCustomerId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullStatus_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "status";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity.status";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setStatus(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullTotal_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "totalPrice";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity.totalPrice";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setTotalPrice(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidEmptyItems_whenCallSave_shouldReturnOrder() {
        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setOrderItems(new HashSet<>());

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getOrderCode(), actualResult.getOrderCode().getValue());
        Assertions.assertEquals(aEntity.getCustomerId(), actualResult.getCustomerId());
        Assertions.assertEquals(aEntity.getStatus(), actualResult.getOrderStatus());
        Assertions.assertEquals(aEntity.getTotalPrice(), actualResult.getTotalAmount());
        Assertions.assertTrue(actualResult.getOrderItems().isEmpty());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualResult.getUpdatedAt());
    }

    @Test
    void givenAValidOrder_whenCallSaveAndToDomain_shouldReturnOrder() {
        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);

        Assertions.assertDoesNotThrow(() -> orderItemJpaEntityRepository.saveAll(aEntity.getOrderItems()));
        final var actualResult = Assertions.assertDoesNotThrow(() -> orderJpaEntityRepository.save(aEntity)
                .toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getOrderCode(), actualResult.getOrderCode().getValue());
        Assertions.assertEquals(aEntity.getCustomerId(), actualResult.getCustomerId());
        Assertions.assertEquals(aEntity.getStatus(), actualResult.getOrderStatus());
        Assertions.assertEquals(aEntity.getTotalPrice(), actualResult.getTotalAmount());
        Assertions.assertEquals(aEntity.getOrderItems().size(), actualResult.getOrderItems().size());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualResult.getUpdatedAt());
    }

    @Test
    void givenAValidNullCouponCodeAndZeroCouponPercentage_whenCallSave_shouldReturnOrder() {
        final var aOrder = Fixture.Orders.orderWithoutCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setCouponCode(null);
        aEntity.setCouponPercentage(0.0f);

        Assertions.assertDoesNotThrow(() -> orderItemJpaEntityRepository.saveAll(aEntity.getOrderItems()));
        final var actualResult = Assertions.assertDoesNotThrow(() -> orderJpaEntityRepository.save(aEntity)
                .toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getOrderCode(), actualResult.getOrderCode().getValue());
        Assertions.assertEquals(aEntity.getCustomerId(), actualResult.getCustomerId());
        Assertions.assertEquals(aEntity.getStatus(), actualResult.getOrderStatus());
        Assertions.assertEquals(aEntity.getTotalPrice(), actualResult.getTotalAmount());
        Assertions.assertEquals(aEntity.getOrderItems().size(), actualResult.getOrderItems().size());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualResult.getUpdatedAt());
    }

    @Test
    void givenAnInvalidNullOrderDeliveryId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "orderDeliveryId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity.orderDeliveryId";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setOrderDeliveryId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullOrderPaymentId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "orderPaymentId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntity.orderPaymentId";

        final var aOrder = Fixture.Orders.orderWithCoupon();
        final var aEntity = OrderJpaEntity.toEntity(aOrder);
        aEntity.setOrderPaymentId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
