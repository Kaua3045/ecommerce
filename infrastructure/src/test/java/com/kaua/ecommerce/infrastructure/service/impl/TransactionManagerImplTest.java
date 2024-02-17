package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@IntegrationTest
public class TransactionManagerImplTest {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void givenAValidProcess_whenCallExecute_thenShouldExecuteActionAndReturnSuccess() {
        final var aMockTransactionTemplate = Mockito.mock(TransactionTemplate.class);
        final var aTransactionImpl = new TransactionManagerImpl(aMockTransactionTemplate);

        Mockito.when(aMockTransactionTemplate.execute(Mockito.any())).thenReturn("success");

        final var aResult = aTransactionImpl.execute(() -> "success");

        Assertions.assertEquals("success", aResult.getSuccessResult());
    }

    @Test
    void givenAValidProcess_whenCallExecuteButThrows_thenShouldExecuteActionAndReturnFailure() {
        final var aMockTransactionTemplate = Mockito.mock(TransactionTemplate.class);
        final var aTransactionImpl = new TransactionManagerImpl(aMockTransactionTemplate);

        Mockito.when(aMockTransactionTemplate.execute(Mockito.any()))
                .thenThrow(new RuntimeException("error"));

        final var aResult = aTransactionImpl.execute(() -> new RuntimeException("error"));

        Assertions.assertTrue(aResult.isFailure());
        Assertions.assertEquals("error", aResult.getErrorResult().message());
    }

    @Test
    void givenAValidProcess_whenCallExecuteAndActionThrowsException_thenShouldRollbackAndReturnFailure() {
        final var aMockAction = Mockito.mock(Supplier.class);
        final var aTransactionImpl = new TransactionManagerImpl(transactionTemplate);

        Mockito.when(aMockAction.get()).thenThrow(new RuntimeException("error"));

        final var aResult = aTransactionImpl.execute(aMockAction);

        Assertions.assertTrue(aResult.isFailure());
        Assertions.assertEquals("error", aResult.getErrorResult().message());
    }
}
