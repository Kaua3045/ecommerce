package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.listeners.models.TestListenerDomainEvent;
import com.kaua.ecommerce.infrastructure.service.EventDatabaseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
public class EventPublisherImplTest {

    @MockBean
    private EventDatabaseService eventDatabaseService;

    @Autowired
    private EventPublisher eventPublisher;

    @Test
    void givenEvent_whenPublish_thenShouldPublishEvent() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());

        // when
        eventPublisher.publish(aDomainEvent);

        // then
        Mockito.verify(eventDatabaseService, Mockito.times(1)).send(aDomainEvent, "");
    }

    @Test
    void givenEvent_whenPublish_thenShouldLogError() {
        // given
        final var aDomainEvent = new TestListenerDomainEvent(IdUtils.generate());

        Mockito.doThrow(new RuntimeException()).when(eventDatabaseService).send(aDomainEvent, "");

        // when
        eventPublisher.publish(aDomainEvent);

        // then
        Mockito.verify(eventDatabaseService, Mockito.times(1)).send(aDomainEvent, "");
    }
}
