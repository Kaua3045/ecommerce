package com.kaua.ecommerce.infrastructure.backpressure;

public enum BackpressureStrategies {

    SQL,
    ELASTICSEARCH,
    CACHE,
    SQL_ELASTICSEARCH_CACHE,
    SQL_CACHE,
    NONE
}
