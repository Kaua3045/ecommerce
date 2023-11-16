package com.kaua.ecommerce.infrastructure.category.persistence;

import com.kaua.ecommerce.infrastructure.category.CategoryEntityElastic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@DataElasticsearchTest
@AutoConfigureDataRedis
@Testcontainers
public class CategoryElasticsearchGatewayTest {

    @Container
    private static final ElasticsearchContainer elastic = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.9"))
            .withExposedPorts(9200);

    @DynamicPropertySource
    public static void elasticsearchProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.elasticsearch.host", elastic::getHttpHostAddress);
        registry.add("spring.data.elasticsearch.username", () -> "elastic");
        registry.add("spring.data.elasticsearch.password", () -> "elastic");
//        registry.add("elasticsearch.usernames", () -> "elastic");
//        registry.add("elasticsearch.passwords", () -> "elastic");
    }

//    @BeforeEach
//    void setUp() {
//        elastic.start();
//    }
//
//    @AfterEach
//    void tearDown() {
//        elastic.stop();
//    }

    @Autowired
    private CategoryEntityElastic categoryElasticsearchRepository;

    @Test
    void testInjection() {
        Assertions.assertNotNull(this.categoryElasticsearchRepository);
    }
}
