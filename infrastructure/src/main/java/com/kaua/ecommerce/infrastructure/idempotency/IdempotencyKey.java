package com.kaua.ecommerce.infrastructure.idempotency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IdempotencyKey {

    String IDEMPOTENCY_KEY_HEADER = "x-idempotency-key";
    String IDEMPOTENCY_RESPONSE_HEADER = "x-idempotency-response";

    long ttl() default 1;

    TimeUnit timeUnit() default TimeUnit.HOURS;
}
