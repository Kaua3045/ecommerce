package com.kaua.ecommerce.application.coupon.update;

import com.kaua.ecommerce.application.usecases.coupon.update.UpdateCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.update.UpdateCouponUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;

@IntegrationTest
public class UpdateCouponUseCaseIT {

    @Autowired
    private UpdateCouponUseCase updateCouponUseCase;

    @Autowired
    private CouponJpaEntityRepository couponJpaEntityRepository;

    @Test
    void givenAValidValues_whenCallUpdateCouponUseCaseExecute_thenCouponShouldBeUpdated() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        final var aCouponId = aCoupon.getId().getValue();

        final var aCode = "NEWCODE";
        final var aPercentage = 200.0f;
        final var aExpirationDate = InstantUtils.now().plus(5, ChronoUnit.DAYS).toString();

        final var aCommand = UpdateCouponCommand.with(aCouponId, aCode, aPercentage, aExpirationDate);

        final var aOutput = this.updateCouponUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCouponId, aOutput.couponId());
        Assertions.assertEquals(aCode, aOutput.code());

        final var aCouponUpdated = this.couponJpaEntityRepository.findById(aCouponId).get().toDomain();

        Assertions.assertEquals(aCode, aCouponUpdated.getCode().getValue());
        Assertions.assertEquals(aPercentage, aCouponUpdated.getPercentage());
        Assertions.assertEquals(aExpirationDate, aCouponUpdated.getExpirationDate().toString());
    }
}
