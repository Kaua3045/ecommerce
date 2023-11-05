package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.AmqpTestConfiguration;
import com.kaua.ecommerce.config.CacheCleanUpExtension;
import com.kaua.ecommerce.config.JpaCleanUpExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration-cache")
@SpringBootTest(classes = { Main.class, AmqpTestConfiguration.class })
@ExtendWith({ CacheCleanUpExtension.class, JpaCleanUpExtension.class })
@Transactional
public @interface CacheGatewayTest {
}
