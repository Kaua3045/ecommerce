package com.kaua.ecommerce.config;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DirtiesContext
public abstract class Neo4JTestConfiguration {

    @Container
    private static final Neo4jContainer<?> neo4j = new Neo4jContainer<>(
            DockerImageName.parse("neo4j:latest"))
            .withAdminPassword("123456789")
            .withExposedPorts(7687);

    static {
        neo4j.start();
    }

    @DynamicPropertySource
    public static void neo4jProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", neo4j::getAdminPassword);
    }
}
