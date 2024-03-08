package com.kaua.ecommerce.application.usecases.coupon.delete;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;

import java.util.Objects;

public class DefaultDeleteCouponUseCase extends DeleteCouponUseCase {

    private final CouponGateway couponGateway;
    private final CouponSlotGateway couponSlotGateway;
    private final TransactionManager transactionManager;

    public DefaultDeleteCouponUseCase(
            final CouponGateway couponGateway,
            final CouponSlotGateway couponSlotGateway,
            final TransactionManager transactionManager
    ) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
        this.couponSlotGateway = Objects.requireNonNull(couponSlotGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public void execute(String aCouponId) {
        final var aTransactionResult = this.transactionManager.execute(() -> {
            this.couponSlotGateway.deleteAllByCouponId(aCouponId);
            this.couponGateway.deleteById(aCouponId);
            return null;
        });

        if (aTransactionResult.isFailure()) {
            throw TransactionFailureException.with(aTransactionResult.getErrorResult());
        }
    }
}
