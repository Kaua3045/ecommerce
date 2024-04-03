package com.kaua.ecommerce.infrastructure.exceptions;

import com.kaua.ecommerce.domain.exceptions.NoStackTraceException;

public class NewerEventProcessedException extends NoStackTraceException {

    private NewerEventProcessedException(final String message) {
        super(message);
    }

    public static String getExceptionMessage(final String payloadId) {
        return new NewerEventProcessedException("A newer event has already been processed for this payloadId: " + payloadId).getMessage();
    }
}
