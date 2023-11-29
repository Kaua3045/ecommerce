package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.DatabaseTestConfiguration;
import com.kaua.ecommerce.config.JpaCleanUpExtension;
import com.kaua.ecommerce.infrastructure.service.impl.MySQLEventDatabaseServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@ComponentScan(
        basePackages = "com.kaua.ecommerce",
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*MySQLGateway")
        }
)
@DataJpaTest
@ExtendWith(JpaCleanUpExtension.class)
@Import({MySQLEventDatabaseServiceImpl.class, DatabaseTestConfiguration.class})
@AutoConfigureDataRedis
public @interface DatabaseGatewayTest {
}
