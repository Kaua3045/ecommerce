package com.kaua.ecommerce.infrastructure.backpressure;

import com.kaua.ecommerce.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@UnitTest
public class BackpressureFactoryTest {

    @Test
    void givenACacheStrategy_whenCallShouldWait_ThenReturnCacheHealthyBackpressureResult() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Assertions.assertFalse(backpressure.shouldWait(BackpressureStrategies.CACHE));
    }

    @Test
    void givenASQLStrategy_whenCallShouldWait_ThenReturnDataSourceSQLHealthyBackpressureResult() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Assertions.assertFalse(backpressure.shouldWait(BackpressureStrategies.SQL));
    }

    @Test
    void givenAnElasticsearchStrategy_whenCallShouldWait_ThenReturnElasticsearchHealthyBackpressureResult() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Assertions.assertFalse(backpressure.shouldWait(BackpressureStrategies.ELASTICSEARCH));
    }

    @Test
    void givenAnSqlElasticsearchCacheStrategy_whenCallShouldWait_ThenReturnFalse() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(false);

        Assertions.assertFalse(backpressure.shouldWait(BackpressureStrategies.SQL_ELASTICSEARCH_CACHE));
    }

    @Test
    void givenAnSqlElasticsearchCacheStrategy_whenCallShouldWait_ThenReturnTrue() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(true);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(true);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(true);

        Assertions.assertTrue(backpressure.shouldWait(BackpressureStrategies.SQL_ELASTICSEARCH_CACHE));
    }

    @Test
    void givenAnSqlElasticsearchCacheStrategy_whenCallShouldWaitButDataSourceHealthyReturnTrue_ThenReturnTrue() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(true);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(false);

        Assertions.assertTrue(backpressure.shouldWait(BackpressureStrategies.SQL_ELASTICSEARCH_CACHE));
    }

    @Test
    void givenAnSqlElasticsearchCacheStrategy_whenCallShouldWaitButCacheHealthyReturnTrue_ThenReturnTrue() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(true);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(false);

        Assertions.assertTrue(backpressure.shouldWait(BackpressureStrategies.SQL_ELASTICSEARCH_CACHE));
    }

    @Test
    void givenAnSqlElasticsearchCacheStrategy_whenCallShouldWaitButElasticsearchHealthyReturnTrue_ThenReturnTrue() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(true);

        Assertions.assertTrue(backpressure.shouldWait(BackpressureStrategies.SQL_ELASTICSEARCH_CACHE));
    }

    @Test
    void givenAnNoneStrategy_whenCallShouldWait_ThenReturnFalse() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Assertions.assertFalse(backpressure.shouldWait(BackpressureStrategies.NONE));
    }

    @Test
    void givenAnSqlCacheStrategy_whenCallShouldWait_ThenReturnFalse() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(false);

        Assertions.assertFalse(backpressure.shouldWait(BackpressureStrategies.SQL_CACHE));
    }

    @Test
    void givenAnSqlCacheStrategy_whenCallShouldWait_ThenReturnTrue() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(true);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(true);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(false);

        Assertions.assertTrue(backpressure.shouldWait(BackpressureStrategies.SQL_CACHE));
    }

    @Test
    void givenAnSqlCacheStrategy_whenCallShouldWaitButDataSourceHealthyReturnTrue_ThenReturnTrue() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(true);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(false);

        Assertions.assertTrue(backpressure.shouldWait(BackpressureStrategies.SQL_CACHE));
    }

    @Test
    void givenAnSqlCacheStrategy_whenCallShouldWaitButCacheHealthyReturnTrue_ThenReturnTrue() {
        final var aMockedCacheHealthyBackpressure = Mockito.mock(CacheHealthyBackpressure.class);
        final var aMockedDataSourceSQLHealthyBackpressure = Mockito.mock(DataSourceSQLHealthyBackpressure.class);
        final var aMockedElasticsearchHealthyBackpressure = Mockito.mock(ElasticsearchHealthyBackpressure.class);

        final var backpressure = new BackpressureFactory(
                aMockedCacheHealthyBackpressure,
                aMockedDataSourceSQLHealthyBackpressure,
                aMockedElasticsearchHealthyBackpressure
        );

        Mockito.when(aMockedCacheHealthyBackpressure.shouldWait()).thenReturn(true);
        Mockito.when(aMockedDataSourceSQLHealthyBackpressure.shouldWait()).thenReturn(false);
        Mockito.when(aMockedElasticsearchHealthyBackpressure.shouldWait()).thenReturn(false);

        Assertions.assertTrue(backpressure.shouldWait(BackpressureStrategies.SQL_CACHE));
    }
}
