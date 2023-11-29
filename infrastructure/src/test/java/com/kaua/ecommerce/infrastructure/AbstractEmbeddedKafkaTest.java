package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.AmqpTestConfiguration;
import com.kaua.ecommerce.config.IntegrationTestConfiguration;
import com.kaua.ecommerce.config.JpaCleanUpExtension;
import com.kaua.ecommerce.infrastructure.listeners.models.Source;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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

@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ActiveProfiles("test-integration-kafka")
@EnableAutoConfiguration(exclude = {ElasticsearchRepositoriesAutoConfiguration.class})
@SpringBootTest(
        classes = {Main.class, AmqpTestConfiguration.class, IntegrationTestConfiguration.class},
        properties = {"kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith(JpaCleanUpExtension.class)
public abstract class AbstractEmbeddedKafkaTest {

    @Autowired
    protected EmbeddedKafkaBroker kafkaBroker;

    private Producer<String, String> producer;
    private AdminClient admin;

    @BeforeAll
    void init() {
//        admin = AdminClient.create(Collections.singletonMap(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker.getBrokersAsString()));

        producer =
                new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(kafkaBroker), new StringSerializer(), new StringSerializer())
                        .createProducer();
    }

    @AfterAll
    void shutdown() {
        producer.close();
//        admin.close();
        kafkaBroker.destroy();
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
}
