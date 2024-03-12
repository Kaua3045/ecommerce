package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.AmqpTestConfiguration;
import com.kaua.ecommerce.config.IntegrationTestConfiguration;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@EnableAutoConfiguration(exclude = ElasticsearchRepositoriesAutoConfiguration.class)
@SpringBootTest(classes = {Main.class, AmqpTestConfiguration.class, IntegrationTestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Tag("integrationTest")
public @interface AmqpTest {
}
