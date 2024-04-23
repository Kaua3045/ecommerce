package com.kaua.ecommerce.infrastructure.backpressure;

import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("dataSourceSQLHealthyBackpressure")
public class DataSourceSQLHealthyBackpressure implements Backpressure {

    private final DataSourceHealthIndicator dataSourceHealthIndicator;

    public DataSourceSQLHealthyBackpressure(final DataSourceHealthIndicator dataSourceHealthIndicator) {
        this.dataSourceHealthIndicator = Objects.requireNonNull(dataSourceHealthIndicator);
    }

    @Override
    public boolean shouldWait() {
        return this.dataSourceHealthIndicator.health().getStatus() != Status.UP;
    }
}
