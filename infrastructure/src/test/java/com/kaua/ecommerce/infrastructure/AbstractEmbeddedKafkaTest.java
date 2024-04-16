package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.IntegrationTestConfiguration;
import com.kaua.ecommerce.infrastructure.configurations.properties.kafka.KafkaProperties;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventEntity;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicCollection;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ActiveProfiles("test-integration-kafka")
@EnableAutoConfiguration(exclude = {ElasticsearchRepositoriesAutoConfiguration.class})
@SpringBootTest(
        classes = {Main.class, IntegrationTestConfiguration.class},
        properties = {"kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Tag("heavyIntegrationTest")
public abstract class AbstractEmbeddedKafkaTest {

    @Autowired
    protected EmbeddedKafkaBroker kafkaBroker;

    @Autowired
    private KafkaProperties kafkaProperties;

    private Producer<String, String> producer;
    private AdminClient admin;

    @BeforeAll
    void init() {
        admin = AdminClient.create(Collections.singletonMap(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker.getBrokersAsString()));

        producer = new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(kafkaBroker), new StringSerializer(), new StringSerializer())
                .createProducer();
    }

    @AfterAll
    void shutdown() {
        producer.close();

//        admin.close();
//        kafkaBroker.destroy();
    }

    protected AdminClient admin() {
        return admin;
    }

    protected Producer<String, String> producer() {
        return producer;
    }

    protected ProducerRecord<String, String> createProducerRecord(
            final String aTopic,
            final String aMessage,
            final OutboxEventEntity aOutboxEntity
    ) {
        final var aProducerRecord = new ProducerRecord<String, String>(aTopic, aMessage);
        aProducerRecord.headers().add("event_id", aOutboxEntity.getEventId().getBytes());
        aProducerRecord.headers().add("event_type", aOutboxEntity.getEventType().getBytes());
        aProducerRecord.headers().add("event_occurred_on", aOutboxEntity.getOccurredOn().toString().getBytes());
        return aProducerRecord;
    }

    protected ConsumerRecord<String, String> createConsumerRecord(
            final String aTopic,
            final String aMessage,
            final OutboxEventEntity aOutboxEntity
    ) {
        final var aConsumerRecord = new ConsumerRecord<String, String>(aTopic, 1, 0, null, aMessage);
        aConsumerRecord.headers().add("event_id", aOutboxEntity.getEventId().getBytes());
        aConsumerRecord.headers().add("event_type", aOutboxEntity.getEventType().getBytes());
        aConsumerRecord.headers().add("event_occurred_on", aOutboxEntity.getOccurredOn().toString().getBytes());
        return aConsumerRecord;
    }

    protected void cleanUpTopics(final String prefixName) throws ExecutionException, InterruptedException, TimeoutException {
        final var aTopicsNames = admin().listTopics().listings().get(1, TimeUnit.MINUTES).stream()
                .map(TopicListing::name)
                .filter(it -> it.contains(prefixName))
                .toList();

        admin().deleteTopics(TopicCollection.ofTopicNames(aTopicsNames)).all()
                .get(1, TimeUnit.MINUTES);
    }
}
