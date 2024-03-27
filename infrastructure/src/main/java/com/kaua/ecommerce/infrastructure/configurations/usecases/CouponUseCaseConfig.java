package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.application.usecases.coupon.activate.ActivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.activate.DefaultActivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.create.DefaultCreateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.deactivate.DeactivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.deactivate.DefaultDeactivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.delete.DefaultDeleteCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.delete.DeleteCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.retrieve.list.DefaultListCouponsUseCase;
import com.kaua.ecommerce.application.usecases.coupon.retrieve.list.ListCouponsUseCase;
import com.kaua.ecommerce.application.usecases.coupon.apply.DefaultApplyCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.update.DefaultUpdateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.update.UpdateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.validate.DefaultValidateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.validate.ValidateCouponUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CouponUseCaseConfig {

    private final CouponGateway couponGateway;
    private final CouponSlotGateway couponSlotGateway;
    private final TransactionManager transactionManager;

    public CouponUseCaseConfig(
            final CouponGateway couponGateway,
            final CouponSlotGateway couponSlotGateway,
            final TransactionManager transactionManager
    ) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
        this.couponSlotGateway = Objects.requireNonNull(couponSlotGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Bean
    public CreateCouponUseCase createCouponUseCase() {
        return new DefaultCreateCouponUseCase(couponGateway, couponSlotGateway, transactionManager);
    }

    @Bean
    public ActivateCouponUseCase activateCouponUseCase() {
        return new DefaultActivateCouponUseCase(couponGateway);
    }

    @Bean
    public DeactivateCouponUseCase deactivateCouponUseCase() {
        return new DefaultDeactivateCouponUseCase(couponGateway);
    }

    @Bean
    public DeleteCouponUseCase deleteCouponUseCase() {
        return new DefaultDeleteCouponUseCase(couponGateway, couponSlotGateway, transactionManager);
    }

    @Bean
    public ValidateCouponUseCase validateCouponUseCase() {
        return new DefaultValidateCouponUseCase(couponGateway, couponSlotGateway);
    }

    @Bean
    public ApplyCouponUseCase removeCouponSlotUseCase() {
        return new DefaultApplyCouponUseCase(couponGateway, couponSlotGateway);
    }

    @Bean
    public ListCouponsUseCase listCouponsUseCase() {
        return new DefaultListCouponsUseCase(couponGateway);
    }

    @Bean
    public UpdateCouponUseCase updateCouponUseCase() {
        return new DefaultUpdateCouponUseCase(couponGateway);
    }
}
