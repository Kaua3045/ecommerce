package com.kaua.ecommerce.application.adapters.responses;

import com.kaua.ecommerce.domain.validation.Error;

public class TransactionResult<T> {

    private final T successResult;
    private final Error errorResult;

    private TransactionResult(T aSuccessResult, Error aErrorResult) {
        this.successResult = aSuccessResult;
        this.errorResult = aErrorResult;
    }

    public static <T> TransactionResult<T> success(T aSuccessResult) {
        return new TransactionResult<>(aSuccessResult, null);
    }

    public static <T> TransactionResult<T> failure(Error aErrorResult) {
        return new TransactionResult<>(null, aErrorResult);
    }

    public boolean isFailure() {
        return this.errorResult != null;
    }

    public T getSuccessResult() {
        return this.successResult;
    }

    public Error getErrorResult() {
        return this.errorResult;
    }
}
