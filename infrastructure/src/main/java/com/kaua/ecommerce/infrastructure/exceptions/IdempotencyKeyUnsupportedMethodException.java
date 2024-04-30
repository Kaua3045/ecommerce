package com.kaua.ecommerce.infrastructure.exceptions;

import com.kaua.ecommerce.domain.exceptions.NoStackTraceException;

public class IdempotencyKeyUnsupportedMethodException extends NoStackTraceException {

    public IdempotencyKeyUnsupportedMethodException(final String method) {
        super("Idempotency key is not supported for this method: " + method);
    }
}
