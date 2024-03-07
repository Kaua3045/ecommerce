package com.kaua.ecommerce.infrastructure.coupon.slot.persistence;

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
public class CouponSlotJpaEntityRepositoryTest {

    @Autowired
    private CouponSlotJpaEntityRepository couponSlotJpaRepository;

    @Test
    void givenAnInvalidNullCouponId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "couponId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntity.couponId";

        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(Fixture.Coupons.limitedCouponActivated());

        final var aEntity = CouponSlotJpaEntity.toEntity(aCouponSlot);
        aEntity.setCouponId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> couponSlotJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntity";

        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(Fixture.Coupons.limitedCouponActivated());

        final var aEntity = CouponSlotJpaEntity.toEntity(aCouponSlot);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> couponSlotJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
