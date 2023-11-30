package com.kaua.ecommerce.domain.exceptions;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotFoundExceptionTest extends UnitTest {

    @Test
    void givenAValidAggregate_whenCallNotFoundExceptionWith_ThenReturnNotFoundException() {
        // given
        final var aggregate = Customer.class;
        final var aId = "123";
        final var expectedErrorMessage = "Customer with id 123 was not found";

        // when
        final var notFoundException = NotFoundException.with(aggregate, aId);
        // then
        Assertions.assertEquals(expectedErrorMessage, notFoundException.get().getMessage());
    }
}
