package com.kaua.ecommerce.infrastructure.utils;

import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ApiErrorTest {

    @Test
    void givenAValidDomainException_whenCallFrom_shouldReturnMessageAndErrorList() {
        final var aErrors = List.of(new Error("Error 1"), new Error("Error 2"));
        final var aDomainException = DomainException.with(aErrors);

        final var aResult = ApiError.from(aDomainException);

        Assertions.assertEquals(aDomainException.getMessage(), aResult.message());
        Assertions.assertEquals(aErrors, aResult.errors());
    }

    @Test
    void givenAValidMessage_whenCallFrom_shouldReturnMessageAndEmptyErrorList() {
        final var aMessage = "Internal Server Error";

        final var aResult = ApiError.from(aMessage);

        Assertions.assertEquals(aMessage, aResult.message());
        Assertions.assertEquals(0, aResult.errors().size());
    }
}
