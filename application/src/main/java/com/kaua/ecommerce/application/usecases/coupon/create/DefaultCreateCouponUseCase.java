package com.kaua.ecommerce.application.usecases.coupon.create;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponSlot;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultCreateCouponUseCase extends CreateCouponUseCase {

    private final CouponGateway couponGateway;
    private final CouponSlotGateway couponSlotGateway;
    private final TransactionManager transactionManager;

    public DefaultCreateCouponUseCase(
            final CouponGateway couponGateway,
            final CouponSlotGateway couponSlotGateway,
            final TransactionManager transactionManager
    ) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
        this.couponSlotGateway = Objects.requireNonNull(couponSlotGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public Either<NotificationHandler, CreateCouponOutput> execute(CreateCouponCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.couponGateway.existsByCode(input.code())) {
            return Either.left(aNotification.append(new Error("'code' already exists")));
        }

        final var aCouponType = getCouponType(input.type());

        final var aCoupon = Coupon.newCoupon(
                input.code(),
                input.percentage(),
                input.minimumPurchase(),
                InstantUtils.parse(input.expirationDate()),
                input.isActive(),
                aCouponType
        );

        aCoupon.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        if (aCouponType.name().equals(CouponType.LIMITED.name())) {
            if (input.maxUses() <= 0) {
                return Either.left(aNotification.append(
                        new Error(CommonErrorMessage.greaterThan("maxUses", 0))
                ));
            }

            final var aCouponSlots = createCouponSlots(input.maxUses(), aCoupon.getId().getValue());

            final var aTransactionResult = this.transactionManager.execute(() -> {
                final var aResult = this.couponGateway.create(aCoupon);
                this.couponSlotGateway.createInBatch(aCouponSlots);
                return aResult;
            });

            if (aTransactionResult.isFailure()) {
                throw TransactionFailureException.with(aTransactionResult.getErrorResult());
            }

            return Either.right(CreateCouponOutput.from(aCoupon));
        }

        final var aTransactionResult = this.transactionManager.execute(() -> this.couponGateway.create(aCoupon));

        if (aTransactionResult.isFailure()) {
            throw TransactionFailureException.with(aTransactionResult.getErrorResult());
        }

        return Either.right(CreateCouponOutput.from(aCoupon));
    }

    private CouponType getCouponType(String type) {
        return CouponType.of(type)
                .orElseThrow(() -> DomainException
                        .with(new Error("type %s was not found".formatted(type))));
    }

    private Set<CouponSlot> createCouponSlots(final int maxUses, final String aCouponId) {
        return IntStream.range(0, maxUses)
                .mapToObj(i -> CouponSlot.newCouponSlot(aCouponId))
                .collect(Collectors.toSet());
    }
}
