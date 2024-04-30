package com.kaua.ecommerce.infrastructure.idempotency;

import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.config.ConcurrentlyTestHelper;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.exceptions.IdempotencyKeyAlreadyExistsException;
import com.kaua.ecommerce.infrastructure.exceptions.InternalServerErrorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@CacheGatewayTest
public class IdempotencyKeyGatewayTest extends CacheTestConfiguration {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IdempotencyKeyGateway idempotencyKeyGateway;

    @BeforeEach
    void cleanUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void givenAValidIdempotencyKeyWithBody_whenSave_thenShouldSave() {
        final var aIdempotencyKey = IdUtils.generateWithoutDash();
        final var aBody = new IdempotencyKeyBodyTest("test");
        final var aHeaders = new HashMap<String, String>();
        aHeaders.put("location", "/test/123");
        aHeaders.put("content-type", MediaType.APPLICATION_JSON_VALUE);

        final var aIdempotencyKeyInput = new IdempotencyKeyInput(200, aBody, aHeaders);

        Assertions.assertTrue(this.idempotencyKeyGateway.get(aIdempotencyKey).isEmpty());

        this.idempotencyKeyGateway.save(
                aIdempotencyKey,
                aIdempotencyKeyInput,
                1,
                TimeUnit.HOURS
        );

        final var aResult = this.redisTemplate.opsForHash().entries("idempotency:" + aIdempotencyKey);

        Assertions.assertEquals(3, aResult.size());
        Assertions.assertNotNull(aResult.get("body"));
        Assertions.assertEquals(200, (int) aResult.get("statusCode"));
        Assertions.assertNotNull(aResult.get("headers"));
    }

    @Test
    void givenAnExistsIdempotencyKey_whenSave_thenShouldThrowException() throws InterruptedException {
        final var aIdempotencyKey = IdUtils.generateWithoutDash();
        final var aBody = new IdempotencyKeyBodyTest("test");

        final var aHeaders = new HashMap<String, String>();
        aHeaders.put("location", "/test/123");
        aHeaders.put("content-type", MediaType.APPLICATION_JSON_VALUE);

        final var aIdempotencyKeyInput = new IdempotencyKeyInput(200, aBody, aHeaders);

        Assertions.assertTrue(this.idempotencyKeyGateway.get(aIdempotencyKey).isEmpty());

        this.idempotencyKeyGateway.save(
                aIdempotencyKey,
                1,
                TimeUnit.HOURS
        );

        ConcurrentlyTestHelper.doSyncAndConcurrently(
                2,
                o -> this.idempotencyKeyGateway.save(
                        aIdempotencyKey,
                        aIdempotencyKeyInput,
                        1,
                        TimeUnit.HOURS
                ),
                IdempotencyKeyGatewayTest.class.getSimpleName(),
                1,
                1
        );
    }

    @Test
    void givenAnInvalidBody_whenSave_thenShouldThrowException() {
        final var aIdempotencyKey = IdUtils.generateWithoutDash();
        final var aBody = "test";

        Assertions.assertTrue(this.idempotencyKeyGateway.get(aIdempotencyKey).isEmpty());

        Assertions.assertThrows(InternalServerErrorException.class, () ->
                this.idempotencyKeyGateway.save(
                        aIdempotencyKey,
                        aBody,
                        1,
                        TimeUnit.HOURS
                ));
    }

    @Test
    void givenAnExistsIdempotencyKey_whenGet_thenShouldReturn() {
        final var aIdempotencyKey = IdUtils.generateWithoutDash();
        final var aBody = new IdempotencyKeyBodyTest("test");

        final var aHeaders = new HashMap<String, String>();
        aHeaders.put("location", "/test/123");
        aHeaders.put("content-type", MediaType.APPLICATION_JSON_VALUE);

        final var aIdempotencyKeyInput = new IdempotencyKeyInput(200, String.valueOf(aBody), aHeaders);

        Assertions.assertTrue(this.idempotencyKeyGateway.get(aIdempotencyKey).isEmpty());

        this.idempotencyKeyGateway.save(
                aIdempotencyKey,
                aIdempotencyKeyInput,
                1,
                TimeUnit.HOURS
        );

        final var aResult = this.idempotencyKeyGateway.get(aIdempotencyKey).get();

        Assertions.assertNotNull(aResult.getBody());
        Assertions.assertEquals(aIdempotencyKeyInput.getStatusCode(), aResult.getStatusCode());
        Assertions.assertEquals(aIdempotencyKeyInput.getHeaders().size(), aResult.getHeaders().size());
    }

    @Test
    void givenAnNotExistsIdempotencyKey_whenGet_thenShouldReturnEmpty() {
        final var aIdempotencyKey = IdUtils.generateWithoutDash();

        final var aResult = this.idempotencyKeyGateway.get(aIdempotencyKey);

        Assertions.assertTrue(aResult.isEmpty());
    }

    @Test
    void givenAValidIdempotencyKey_whenSave_thenShouldSave() {
        final var aIdempotencyKey = IdUtils.generateWithoutDash();

        Assertions.assertTrue(this.idempotencyKeyGateway.get(aIdempotencyKey).isEmpty());

        this.idempotencyKeyGateway.save(
                aIdempotencyKey,
                1,
                TimeUnit.HOURS
        );

        final var aResult = this.redisTemplate.opsForHash().entries("idempotency:" + aIdempotencyKey);

        Assertions.assertEquals(3, aResult.size());
        Assertions.assertNotNull(aResult.get("body"));
        Assertions.assertEquals("", aResult.get("statusCode"));
        Assertions.assertNotNull(aResult.get("headers"));
    }

    @Test
    void testConcurrencySaveWithoutBody() throws InterruptedException {
        final var aIdempotencyKey = IdUtils.generateWithoutDash();

        Assertions.assertTrue(this.idempotencyKeyGateway.get(aIdempotencyKey).isEmpty());

        ConcurrentlyTestHelper.doSyncAndConcurrently(
                2,
                o -> this.idempotencyKeyGateway.save(
                        aIdempotencyKey,
                        1,
                        TimeUnit.HOURS
                ),
                IdempotencyKeyGatewayTest.class.getSimpleName(),
                1,
                1
        );
    }

    @Test
    void givenAnExistsIdempotencyKeyWithoutBody_whenSave_thenReturnFalseAndThrowIdempotencyKeyAlreadyExists() {
        final var aIdempotencyKey = IdUtils.generateWithoutDash();

        Assertions.assertTrue(this.idempotencyKeyGateway.get(aIdempotencyKey).isEmpty());

        this.idempotencyKeyGateway.save(
                aIdempotencyKey,
                1,
                TimeUnit.HOURS
        );

        Assertions.assertThrows(IdempotencyKeyAlreadyExistsException.class, () ->
                this.idempotencyKeyGateway.save(
                        aIdempotencyKey,
                        1,
                        TimeUnit.HOURS
                ));
    }
}
