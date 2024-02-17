package com.kaua.ecommerce.infrastructure.utils;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import org.slf4j.Logger;

public final class LogControllerResult {

    private static final String LOG_RESULT = "[{}]: {} result: {}";

    private LogControllerResult() {
    }

    public static void logResult(
            final Logger logger,
            final Class<? extends AggregateRoot<?>> anAggregate,
            final String aAction,
            final Object aResult
    ) {
        if (aResult instanceof Either<?, ?> eitherResult) {
            final var aEitherLeft = eitherResult.isLeft()
                    ? eitherResult.getLeft()
                    : null;

            if (aEitherLeft instanceof NotificationHandler notificationHandler) {
                logger.info(LOG_RESULT, anAggregate.getSimpleName(),
                        aAction,
                        notificationHandler.getErrors());
                return;
            }

            logger.info(LOG_RESULT, anAggregate.getSimpleName(),
                    aAction,
                    eitherResult.isLeft()
                            ? eitherResult.getLeft()
                            : eitherResult.getRight());
            return;
        }

        logger.info(LOG_RESULT, anAggregate.getSimpleName(), aAction, aResult);
    }
}
