package com.kaua.ecommerce.application.exceptions;

import com.kaua.ecommerce.domain.exceptions.NoStackTraceException;
import com.kaua.ecommerce.domain.validation.Error;

public class TransactionFailureException extends NoStackTraceException {

    protected TransactionFailureException(String aMessage) {
        super(aMessage);
    }

    public static TransactionFailureException with(Error aError) {
        return new TransactionFailureException(aError.message());
    }
}
