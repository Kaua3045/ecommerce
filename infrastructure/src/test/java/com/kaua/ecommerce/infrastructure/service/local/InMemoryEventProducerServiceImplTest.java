package com.kaua.ecommerce.infrastructure.service.local;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import com.kaua.ecommerce.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@UnitTest
public class InMemoryEventProducerServiceImplTest {

    private final InMemoryEventProducerServiceImpl target = new InMemoryEventProducerServiceImpl();

    @Test
    void givenAValidObject_whenCallSendEvent_shouldDoesNotThrow() {
        final var aCategory = Fixture.Categories.tech();
        final var aEvent = CategoryCreatedEvent.from(aCategory);

        Assertions.assertDoesNotThrow(() -> this.target.send(aEvent, "categories"));
    }
}
