package com.kaua.ecommerce.infrastructure.exceptions;

import com.kaua.ecommerce.domain.exceptions.NoStackTraceException;

public class InternalServerErrorException extends NoStackTraceException {

    private InternalServerErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static InternalServerErrorException with(final String message, final Throwable cause) {
        return new InternalServerErrorException(message, cause);
    }
}
