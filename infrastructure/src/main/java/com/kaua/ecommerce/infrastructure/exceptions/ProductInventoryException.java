package com.kaua.ecommerce.infrastructure.exceptions;

import com.kaua.ecommerce.domain.exceptions.NoStackTraceException;

public class ProductInventoryException extends NoStackTraceException {

    private ProductInventoryException(final String message, final Throwable exception) {
        super(message, exception);
    }

    public static ProductInventoryException with(final String message, final Throwable exception) {
        return new ProductInventoryException(message, exception);
    }
}
