package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponUseCase;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.infrastructure.api.CouponAPI;
import com.kaua.ecommerce.infrastructure.coupon.models.CreateCouponInput;
import com.kaua.ecommerce.infrastructure.utils.LogControllerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponController implements CouponAPI {

    private static final Logger log = LoggerFactory.getLogger(CouponController.class);

    private final CreateCouponUseCase createCouponUseCase;

    public CouponController(final CreateCouponUseCase createCouponUseCase) {
        this.createCouponUseCase = createCouponUseCase;
    }

    @Override
    public ResponseEntity<?> createCoupon(CreateCouponInput body) {
        final var aCommand = CreateCouponCommand.with(
                body.code(),
                body.percentage(),
                body.expirationDate(),
                body.isActive(),
                body.type(),
                body.maxUses()
        );

        final var aResult = this.createCouponUseCase.execute(aCommand);

        LogControllerResult.logResult(
                log,
                Coupon.class,
                "createCoupon",
                aResult
        );

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.CREATED).body(aResult.getRight());
    }
}
