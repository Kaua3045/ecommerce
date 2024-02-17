package com.kaua.ecommerce.application.adapters;

import com.kaua.ecommerce.application.adapters.responses.TransactionResult;

import java.util.function.Supplier;

public interface TransactionManager {

    <T> TransactionResult<T> execute(Supplier<T> action);
}
