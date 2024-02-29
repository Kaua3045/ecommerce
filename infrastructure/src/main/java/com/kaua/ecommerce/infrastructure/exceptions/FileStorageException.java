package com.kaua.ecommerce.infrastructure.exceptions;

import com.kaua.ecommerce.domain.exceptions.NoStackTraceException;

public class FileStorageException extends NoStackTraceException {

    private FileStorageException(final String message, final Throwable exception) {
        super(message, exception);
    }

    public static FileStorageException with(final String message, final Throwable exception) {
        return new FileStorageException(message, exception);
    }
}
