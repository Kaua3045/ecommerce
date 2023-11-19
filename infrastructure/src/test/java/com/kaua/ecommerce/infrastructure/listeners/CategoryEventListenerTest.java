package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchRepository;
import com.kaua.ecommerce.infrastructure.configurations.annotations.CategoryEvents;
import com.kaua.ecommerce.infrastructure.configurations.properties.kafka.KafkaTopicProperty;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

public class CategoryEventListenerTest extends AbstractEmbeddedKafkaTest {

    @Autowired
    @CategoryEvents
    private KafkaTopicProperty categoryTopicProperty;

    @Autowired
    private CategoryElasticsearchRepository categoryElasticsearchRepository;

    @Test
    void givenAnInvalidEvent_whenReceive_shouldDoNothing() {
        final var aCategoryTest = new TestCategoryListener(CategoryID.unique().getValue());
        final var aProducerRecord = new ProducerRecord<String, Object>(
                categoryTopicProperty.getTopicName(),
                aCategoryTest);

        producer().send(aProducerRecord);
        producer().flush();

        Assertions.assertEquals(0, this.categoryElasticsearchRepository.count());
    }

    @Test
    void givenAValidEventButEventTypeDoesNotMatch_whenReceive_shouldDoNothing() {
        final var aCategoryTest = new TestCategoryListenerDomainEvent(CategoryID.unique().getValue());
        final var aProducerRecord = new ProducerRecord<String, Object>(
                categoryTopicProperty.getTopicName(),
                aCategoryTest);

        producer().send(aProducerRecord);
        producer().flush();

        Assertions.assertEquals(0, this.categoryElasticsearchRepository.count());
    }

    record TestCategoryListener(String id) {}

    record TestCategoryListenerDomainEvent(String id, String eventType, Instant occurredOn)
            implements DomainEvent {

        public TestCategoryListenerDomainEvent(String id) {
            this(id, "test", Instant.now());
        }
    }
}
