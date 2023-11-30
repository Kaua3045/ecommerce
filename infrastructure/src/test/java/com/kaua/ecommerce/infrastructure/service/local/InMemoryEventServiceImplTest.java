package com.kaua.ecommerce.infrastructure.service.local;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unitTest")
public class InMemoryEventServiceImplTest {

    private final InMemoryEventServiceImpl target = new InMemoryEventServiceImpl();

    @Test
    void givenAValidObject_whenCallSendEvent_shouldDoesNotThrow() {
        final var aCategory = Fixture.Categories.tech();
        final var aEvent = CategoryCreatedEvent.from(aCategory);

        Assertions.assertDoesNotThrow(() -> this.target.send(aEvent, "categories"));
    }
}
