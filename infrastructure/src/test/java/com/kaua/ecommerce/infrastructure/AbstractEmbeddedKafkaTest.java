package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.IntegrationTestConfiguration;
import com.kaua.ecommerce.infrastructure.configurations.properties.kafka.KafkaProperties;
import com.kaua.ecommerce.infrastructure.listeners.models.Source;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private KafkaConsumer<String, String> consumer;

    @BeforeAll
    void init() {
        admin = AdminClient.create(Collections.singletonMap(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker.getBrokersAsString()));

        producer = new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(kafkaBroker), new StringSerializer(), new StringSerializer())
                .createProducer();
    }

    @AfterAll
    void shutdown() {
        producer.close();

        if (consumer != null) {
            consumer.close();
        }
//        admin.close();
//        kafkaBroker.destroy();
    }

    protected AdminClient admin() {
        return admin;
    }

    protected Producer<String, String> producer() {
        return producer;
    }

    protected Source aSource() {
        return new Source("ecommerce-mysql", "ecommerce", "outbox");
    }

    protected Map<String, Object> consumerConfigs(final String groupId) {
        final var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, kafkaProperties.isAutoCreateTopics());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.isAutoCommit());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return props;
    }

    protected void cleanUpMessages(final String groupId, final List<String> topics) {
        consumer = new KafkaConsumer<>(consumerConfigs(groupId));

        consumer.subscribe(topics);

        while (true) {
            final var records = consumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) {
                consumer.close();
                break;
            }

            consumer.commitSync();
        }
    }
}
