package com.kaua.ecommerce.infrastructure.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class CategoryElasticsearchInitializer implements CommandLineRunner {

    public static final String INDEX_NAME = "categories";

    private static final Logger log = LoggerFactory.getLogger(CategoryElasticsearchInitializer.class);

    private final ElasticsearchOperations elasticsearchOperations;

    private CategoryElasticsearchInitializer(final ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = Objects.requireNonNull(elasticsearchOperations);
    }

    @Override
    public void run(String... args) throws Exception {
        final var indexCoordinates = IndexCoordinates.of(INDEX_NAME);

        if (isElasticsearchRunning() && !this.elasticsearchOperations.indexOps(indexCoordinates).exists()) {
            Map<String, Object> settings = new HashMap<>();
            final var mappings = Document.create();

            Map<String, Object> properties = new HashMap<>();

            properties.put("id", Map.of("type", "keyword"));
            properties.put("name", Map.of(
                    "type", "text",
                    "fields", Map.of(
                            "keyword", Map.of("type", "keyword")
                    )
            ));
            properties.put("description", Map.of("type", "text"));
            properties.put("slug", Map.of("type", "text"));
            properties.put("parent_id", Map.of("type", "keyword"));
            properties.put("subCategories", Map.of("type", "nested", "properties", subSubCategories()));
            properties.put("sub_categories_level", Map.of("type", "integer"));
            properties.put("created_at", Map.of("type", "date"));
            properties.put("updated_at", Map.of("type", "date"));

            mappings.put("properties", properties);

            this.elasticsearchOperations.indexOps(indexCoordinates).create(settings, mappings);

            log.info("Creating index: {}", INDEX_NAME);
        }
    }

    private Map<String, Object> subSubCategories() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("id", Map.of("type", "keyword"));
        properties.put("name", Map.of(
                "type", "text",
                "fields", Map.of(
                        "keyword", Map.of("type", "keyword")
                )
        ));
        properties.put("description", Map.of("type", "text"));
        properties.put("slug", Map.of("type", "text"));
        properties.put("parent_id", Map.of("type", "keyword"));
        properties.put("subCategories", Map.of("type", "nested"));
        properties.put("sub_categories_level", Map.of("type", "integer"));
        properties.put("created_at", Map.of("type", "date"));
        properties.put("updated_at", Map.of("type", "date"));
        return properties;
    }

    private boolean isElasticsearchRunning() {
        try {
            this.elasticsearchOperations.cluster().health();
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
}
