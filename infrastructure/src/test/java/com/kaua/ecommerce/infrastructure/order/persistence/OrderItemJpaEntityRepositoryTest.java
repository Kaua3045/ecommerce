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
public class OrderItemJpaEntityRepositoryTest {

    @Autowired
    private OrderItemJpaEntityRepository orderItemJpaEntityRepository;

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntity";

        final var aOrderItem = Fixture.Orders.orderItem();
        final var aEntity = OrderItemJpaEntity.toEntity(aOrderItem);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> orderItemJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullOrderId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "orderId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntity.orderId";

        final var aOrderItem = Fixture.Orders.orderItem();
        final var aEntity = OrderItemJpaEntity.toEntity(aOrderItem);
        aEntity.setOrderId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderItemJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullProductId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "productId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntity.productId";

        final var aOrderItem = Fixture.Orders.orderItem();
        final var aEntity = OrderItemJpaEntity.toEntity(aOrderItem);
        aEntity.setProductId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderItemJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullSku_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "sku";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntity.sku";

        final var aOrderItem = Fixture.Orders.orderItem();
        final var aEntity = OrderItemJpaEntity.toEntity(aOrderItem);
        aEntity.setSku(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderItemJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidOneInQuantity_whenCallSave_shouldReturnOrderItem() {
        final var aOrderItem = Fixture.Orders.orderItem();
        final var aEntity = OrderItemJpaEntity.toEntity(aOrderItem);
        aEntity.setQuantity(1);

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderItemJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getOrderItemId());
        Assertions.assertEquals(aEntity.getOrderId(), actualResult.getOrderId());
        Assertions.assertEquals(aEntity.getProductId(), actualResult.getProductId());
        Assertions.assertEquals(aEntity.getQuantity(), actualResult.getQuantity());
    }

    @Test
    void givenAValidOrderItem_whenCallSaveAndToDomain_shouldReturnOrderItem() {
        final var aOrderItem = Fixture.Orders.orderItem();
        final var aEntity = OrderItemJpaEntity.toEntity(aOrderItem);

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderItemJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getOrderItemId());
        Assertions.assertEquals(aEntity.getOrderId(), actualResult.getOrderId());
        Assertions.assertEquals(aEntity.getProductId(), actualResult.getProductId());
        Assertions.assertEquals(aEntity.getQuantity(), actualResult.getQuantity());
    }

    @Test
    void givenAnInvalidNullPrice_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "price";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntity.price";

        final var aOrderItem = Fixture.Orders.orderItem();
        final var aEntity = OrderItemJpaEntity.toEntity(aOrderItem);
        aEntity.setPrice(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderItemJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
