package com.kaua.ecommerce.infrastructure.backpressure;

import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.backpressure.annotations.DataSourceSQLHealthyBackpressureComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;

import javax.sql.DataSource;

@IntegrationTest
public class DataSourceSQLHealthyBackpressureTest {

    @Autowired
    @DataSourceSQLHealthyBackpressureComponent
    private DataSourceSQLHealthyBackpressure dataSourceSQLHealthyBackpressure;

    @Test
    void testDataSourceSQLHealthyBackpressureShouldWaitReturnFalse() {
        Assertions.assertFalse(dataSourceSQLHealthyBackpressure.shouldWait());
    }

    @Test
    void testSourceSQLHealthyBackpressureShouldWaitReturnTrue() {
        final var aMockedDataSource = Mockito.mock(DataSource.class);
        final var aDataSourceSQLHealthyBackpressure = new DataSourceSQLHealthyBackpressure(new DataSourceHealthIndicator(aMockedDataSource));
        Assertions.assertTrue(aDataSourceSQLHealthyBackpressure.shouldWait());
    }
}
