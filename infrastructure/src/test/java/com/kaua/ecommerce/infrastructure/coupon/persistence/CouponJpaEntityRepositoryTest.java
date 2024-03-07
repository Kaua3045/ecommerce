package com.kaua.ecommerce.infrastructure.coupon.persistence;

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
public class CouponJpaEntityRepositoryTest {

    @Autowired
    private CouponJpaEntityRepository couponJpaRepository;

    @Test
    void givenAnInvalidNullCode_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "code";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity.code";

        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        aEntity.setCode(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> couponJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullExpirationDate_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "expirationDate";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity.expirationDate";

        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        aEntity.setExpirationDate(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> couponJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullType_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "type";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity.type";

        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        aEntity.setType(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> couponJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity.createdAt";

        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> couponJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity.updatedAt";

        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> couponJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity";

        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> couponJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidPercentage_whenCallSave_shouldReturnAnEntity() {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        aEntity.setPercentage(5f);

        final var actual = couponJpaRepository.save(aEntity);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(aEntity.getId(), actual.getId());
        Assertions.assertEquals(aEntity.getCode(), actual.getCode());
        Assertions.assertEquals(5f, actual.getPercentage());
        Assertions.assertEquals(aEntity.getExpirationDate(), actual.getExpirationDate());
        Assertions.assertEquals(aEntity.getType(), actual.getType());
        Assertions.assertEquals(aEntity.getCreatedAt(), actual.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actual.getUpdatedAt());
    }

    @Test
    void givenAValidFalseActive_whenCallSave_shouldReturnAnEntity() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        aEntity.setActive(false);

        final var actual = couponJpaRepository.save(aEntity);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(aEntity.getId(), actual.getId());
        Assertions.assertEquals(aEntity.getCode(), actual.getCode());
        Assertions.assertEquals(aEntity.getPercentage(), actual.getPercentage());
        Assertions.assertEquals(aEntity.getExpirationDate(), actual.getExpirationDate());
        Assertions.assertEquals(aEntity.getType(), actual.getType());
        Assertions.assertEquals(aEntity.getCreatedAt(), actual.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actual.getUpdatedAt());
        Assertions.assertFalse(actual.isActive());
    }
}
