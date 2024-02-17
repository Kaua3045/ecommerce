package com.kaua.ecommerce.infrastructure.utils;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UnitTest
public class LogControllerResultTest {

    private final Logger log = LoggerFactory.getLogger(SampleController.class);

    @Test
    void testLogResultWithoutEither() {
        // Arrange
        final var aAggregate = SampleAggregateRoot.class;
        final var aAction = "create";
        final var aResult = "123";

        // Act
        Assertions.assertDoesNotThrow(() ->
                LogControllerResult.logResult(log, aAggregate, aAction, aResult));

        // Assert
        // No exception is expected
    }

    @Test
    void testLogResultWithEitherLeftWithoutNotificationHandler() {
        // Arrange
        final var aAggregate = SampleAggregateRoot.class;
        final var aAction = "update";
        final var aResult = Either.left(NotFoundException.with(new Error("NOT FOUND")));

        // Act
        Assertions.assertDoesNotThrow(() ->
                LogControllerResult.logResult(log, aAggregate, aAction, aResult));

        // Assert
        // No exception is expected
    }

    @Test
    void testLogResultWithEitherLeftWithNotificationHandler() {
        // Arrange
        final var aAggregate = SampleAggregateRoot.class;
        final var aAction = "update";
        final var aResult = Either.left(NotificationHandler.create(new Error("simulated error")));

        // Act
        Assertions.assertDoesNotThrow(() ->
                LogControllerResult.logResult(log, aAggregate, aAction, aResult));

        // Assert
        // No exception is expected
    }

    @Test
    void testLogResultWithEitherRight() {
        // Arrange
        final var aAggregate = SampleAggregateRoot.class;
        final var aAction = "create";
        final var aResult = Either.right("result");

        // Act
        Assertions.assertDoesNotThrow(() ->
                LogControllerResult.logResult(log, aAggregate, aAction, aResult));

        // Assert
        // No exception is expected
    }

    static class SampleController {
    }

    static class SampleAggregateRoot extends AggregateRoot<SampleIdentifier> {
        protected SampleAggregateRoot(SampleIdentifier sampleIdentifier) {
            super(sampleIdentifier);
        }

        @Override
        public void validate(ValidationHandler handler) {

        }
    }

    static class SampleIdentifier extends Identifier {
        private final String value;

        public SampleIdentifier(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
