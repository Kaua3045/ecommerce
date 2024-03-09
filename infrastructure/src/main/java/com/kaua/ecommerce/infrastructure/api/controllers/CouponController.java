package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.coupon.activate.ActivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.deactivate.DeactivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.delete.DeleteCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.validate.ValidateCouponUseCase;
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
    private final ActivateCouponUseCase activateCouponUseCase;
    private final DeactivateCouponUseCase deactivateCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;
    private final ValidateCouponUseCase validateCouponUseCase;

    public CouponController(
            final CreateCouponUseCase createCouponUseCase,
            final ActivateCouponUseCase activateCouponUseCase,
            final DeactivateCouponUseCase deactivateCouponUseCase,
            final DeleteCouponUseCase deleteCouponUseCase,
            final ValidateCouponUseCase validateCouponUseCase
    ) {
        this.createCouponUseCase = createCouponUseCase;
        this.activateCouponUseCase = activateCouponUseCase;
        this.deactivateCouponUseCase = deactivateCouponUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
        this.validateCouponUseCase = validateCouponUseCase;
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

    @Override
    public ResponseEntity<?> validateCouponByCode(String code) {
        final var aResult = this.validateCouponUseCase.execute(code);

        LogControllerResult.logResult(
                log,
                Coupon.class,
                "validateCouponByCode",
                aResult
        );

        return ResponseEntity.ok(aResult);
    }

    @Override
    public ResponseEntity<?> activateCoupon(String id) {
        final var aResult = this.activateCouponUseCase.execute(id);

        LogControllerResult.logResult(
                log,
                Coupon.class,
                "activateCoupon",
                aResult
        );

        return ResponseEntity.ok(aResult);
    }

    @Override
    public ResponseEntity<?> deactivateCoupon(String id) {
        final var aResult = this.deactivateCouponUseCase.execute(id);

        LogControllerResult.logResult(
                log,
                Coupon.class,
                "deactivateCoupon",
                aResult
        );

        return ResponseEntity.ok(aResult);
    }

    @Override
    public void deleteCoupon(String id) {
        this.deleteCouponUseCase.execute(id);
        LogControllerResult.logResult(
                log,
                Coupon.class,
                "deleteCoupon",
                id + " deleted coupon"
        );
    }
}
