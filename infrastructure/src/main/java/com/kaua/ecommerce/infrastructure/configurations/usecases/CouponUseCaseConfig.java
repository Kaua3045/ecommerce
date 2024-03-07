package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.create.DefaultCreateCouponUseCase;
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
}
