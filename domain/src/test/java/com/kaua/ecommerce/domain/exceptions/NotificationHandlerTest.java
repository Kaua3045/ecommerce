package com.kaua.ecommerce.domain.exceptions;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.Validation;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NotificationHandlerTest extends UnitTest {

    @Test
    void givenValidate_whenCallValidate_thenShouldReturnDomainException() {
        final var expectedErrorMessage = "Simulated error";

        final var validation = Mockito.mock(Validation.class);
        Mockito.doThrow(DomainException.with(new Error(expectedErrorMessage)))
                .when(validation)
                .validate();

        final var handler = NotificationHandler.create();

        handler.validate(validation);

        Assertions.assertTrue(handler.hasError());
        Assertions.assertEquals(1, handler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, handler.getErrors().get(0).message());
    }

    @Test
    void givenAValidError_whenCallCreateNotification_thenShouldReturnTrueHasError() {
        // given
        final var error = new Error("Common Error");

        // when
        final var notification = NotificationHandler.create(error);

        // then
        Assertions.assertTrue(notification.hasError());
        Assertions.assertEquals(1, notification.getErrors().size());
    }

    @Test
    void givenAValidErrors_whenCallAppendNotification_thenShouldReturnANotificationWithError() {
        // given
        final var error = new Error("Common Error");

        // when
        final var notification = NotificationHandler.create();
        notification.append(error);

        // then
        Assertions.assertTrue(notification.hasError());
        Assertions.assertEquals(1, notification.getErrors().size());
    }

    @Test
    void givenAValidEmptyError_whenCallAppendNotification_thenShouldReturnANotificationWithoutError() {
        final var notification = NotificationHandler.create();

        // then
        Assertions.assertFalse(notification.hasError());
        Assertions.assertEquals(0, notification.getErrors().size());
    }

    @Test
    void givenAValidValidation_whenCallValidate_thenShouldReturnNotificationEmpty() {
        final String aName = null;

        final var aNotification = NotificationHandler.create();
        final var aValidation = new TestValidation(aName, aNotification);
        aValidation.validate();

        Assertions.assertEquals(1, aNotification.getErrors().size());
    }

    @Test
    void givenAValidValidation_whenCallValidate_thenShouldDoesNotThrow() {
        Validation validation = () -> {};
        NotificationHandler handler = NotificationHandler.create();

        Assertions.assertDoesNotThrow(() -> handler.validate(validation));
    }

    @Test
    void givenAValidValidation_whenCallValidate_thenShouldThrowsException() {
        final var expectedErrorMessage = "Cannot invoke \"com.kaua.ecommerce.domain.validation.Validation.validate()\" because \"aValidation\" is null";
        NotificationHandler handler = NotificationHandler.create();

        Assertions.assertDoesNotThrow(() -> handler.validate(null));
        Assertions.assertEquals(expectedErrorMessage, handler.getErrors().get(0).message());
    }

    @Test
    void givenAValidHandler_whenCallAppend_thenShouldReturnNotification() {
        final var handler = NotificationHandler.create();
        ValidationHandler anotherHandler = new ThrowsValidationHandler();

        Assertions.assertDoesNotThrow(() -> handler.append(anotherHandler));
    }

    static class TestValidation extends Validator {

        private final String name;

        TestValidation(String name, ValidationHandler validationHandler) {
            super(validationHandler);
            this.name = name;
        }

        @Override
        public void validate() {
            if (name == null) {
                this.validationHandler().append(new Error("Name should not be null"));
            }
        }
    }
}
