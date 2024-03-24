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
public class OrderPaymentJpaEntityRepositoryTest {

    @Autowired
    private OrderPaymentJpaEntityRepository orderPaymentJpaEntityRepository;

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.order.persistence.OrderPaymentJpaEntity";

        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aEntity = OrderPaymentJpaEntity.toEntity(aOrderPayment);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> orderPaymentJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullPaymentMethodId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "paymentMethodId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.order.persistence.OrderPaymentJpaEntity.paymentMethodId";

        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aEntity = OrderPaymentJpaEntity.toEntity(aOrderPayment);
        aEntity.setPaymentMethodId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> orderPaymentJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidZeroInstallments_whenCallSave_shouldReturnOrderPayment() {
        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aEntity = OrderPaymentJpaEntity.toEntity(aOrderPayment);
        aEntity.setInstallments(0);

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderPaymentJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getPaymentMethodId(), actualResult.getPaymentMethodId());
        Assertions.assertEquals(aEntity.getInstallments(), actualResult.getInstallments());
    }

    @Test
    void givenAValidOrderPayment_whenCallSaveAndToDomain_shouldReturnOrderPayment() {
        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aEntity = OrderPaymentJpaEntity.toEntity(aOrderPayment);

        final var actualResult = Assertions.assertDoesNotThrow(() -> orderPaymentJpaEntityRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getPaymentMethodId(), actualResult.getPaymentMethodId());
        Assertions.assertEquals(aEntity.getInstallments(), actualResult.getInstallments());
    }
}
