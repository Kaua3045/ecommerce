package com.kaua.ecommerce.domain.exceptions;

import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.Validation;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ThrowsValidationHandlerTest {

    @Test
    void givenAValidError_whenCallAppend_shouldThrowsDomainException() {
        Error error = new Error("Sample error");
        ThrowsValidationHandler handler = new ThrowsValidationHandler();

        Assertions.assertThrows(DomainException.class, () -> handler.append(error));
    }

    @Test
    void givenAValidHandler_whenCallAppend_shouldThrowsDomainException() {
        ThrowsValidationHandler handler = new ThrowsValidationHandler();
        ValidationHandler anotherHandler = new ThrowsValidationHandler();

        Assertions.assertThrows(DomainException.class, () -> handler.append(anotherHandler));
    }

    @Test
    void givenAValidValidation_whenCallValidate_shouldDoesNotThrowException() {
        Validation validation = () -> {};
        ThrowsValidationHandler handler = new ThrowsValidationHandler();

        Assertions.assertDoesNotThrow(() -> handler.validate(validation));
    }

    @Test
    void givenAnInvalidValidation_whenCallValidate_shouldThrowDomainException() {
        ThrowsValidationHandler handler = new ThrowsValidationHandler();

        Assertions.assertThrows(DomainException.class, () -> handler.validate(null));
    }

    @Test
    void testGetErrors() {
        ThrowsValidationHandler handler = new ThrowsValidationHandler();

        Assertions.assertEquals(0, handler.getErrors().size());
    }

    @Test
    void givenAValidError_whenCallHasError_thenReturnFalse() {
        final var testHandler = new TestHasErrorsValidationHandler();
        testHandler.append(new Error("simulated error"));

        ThrowsValidationHandler handler = new ThrowsValidationHandler();

        Assertions.assertFalse(testHandler.hasError());
        Assertions.assertFalse(handler.hasError());
    }

    private class TestHasErrorsValidationHandler implements ValidationHandler {

        private final List<Error> errors;

        public TestHasErrorsValidationHandler() {
            this.errors = new ArrayList<>();
        }
        @Override
        public ValidationHandler append(Error aError) {
            errors.add(aError);
            return this;
        }

        @Override
        public ValidationHandler append(ValidationHandler aHandler) {
            errors.addAll(aHandler.getErrors());
            return this;
        }

        @Override
        public ValidationHandler validate(Validation aValidation) {
            aValidation.validate();
            return this;
        }

        @Override
        public List<Error> getErrors() {
            return null;
        }
    }
}
