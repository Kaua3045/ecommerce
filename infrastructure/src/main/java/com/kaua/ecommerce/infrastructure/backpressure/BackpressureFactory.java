package com.kaua.ecommerce.infrastructure.backpressure;

import com.kaua.ecommerce.infrastructure.backpressure.annotations.CacheHealthyBackpressureComponent;
import com.kaua.ecommerce.infrastructure.backpressure.annotations.DataSourceSQLHealthyBackpressureComponent;
import com.kaua.ecommerce.infrastructure.backpressure.annotations.ElasticsearchHealthyBackpressureComponent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BackpressureFactory {

    private final CacheHealthyBackpressure cacheHealthyBackpressure;
    private final DataSourceSQLHealthyBackpressure dataSourceSQLHealthyBackpressure;
    private final ElasticsearchHealthyBackpressure elasticsearchHealthyBackpressure;

    public BackpressureFactory(
            @CacheHealthyBackpressureComponent CacheHealthyBackpressure cacheHealthyBackpressure,
            @DataSourceSQLHealthyBackpressureComponent DataSourceSQLHealthyBackpressure dataSourceSQLHealthyBackpressure,
            @ElasticsearchHealthyBackpressureComponent ElasticsearchHealthyBackpressure elasticsearchHealthyBackpressure
    ) {
        this.cacheHealthyBackpressure = Objects.requireNonNull(cacheHealthyBackpressure);
        this.dataSourceSQLHealthyBackpressure = Objects.requireNonNull(dataSourceSQLHealthyBackpressure);
        this.elasticsearchHealthyBackpressure = Objects.requireNonNull(elasticsearchHealthyBackpressure);
    }

    public boolean shouldWait(BackpressureStrategies strategy) {
        switch (strategy) {
            case CACHE -> {
                return cacheHealthyBackpressure.shouldWait();
            }
            case SQL -> {
                return dataSourceSQLHealthyBackpressure.shouldWait();
            }
            case ELASTICSEARCH -> {
                return elasticsearchHealthyBackpressure.shouldWait();
            }
            case SQL_ELASTICSEARCH_CACHE -> {
                int shouldWaitCount = 0;
                if (cacheHealthyBackpressure.shouldWait()) shouldWaitCount++;
                if (dataSourceSQLHealthyBackpressure.shouldWait()) shouldWaitCount++;
                if (elasticsearchHealthyBackpressure.shouldWait()) shouldWaitCount++;

                return shouldWaitCount >= 1;
            }
            case SQL_CACHE -> {
                int shouldWaitCount = 0;
                if (cacheHealthyBackpressure.shouldWait()) shouldWaitCount++;
                if (dataSourceSQLHealthyBackpressure.shouldWait()) shouldWaitCount++;

                return shouldWaitCount >= 1;
            }
        }
        return false;
    }
}
