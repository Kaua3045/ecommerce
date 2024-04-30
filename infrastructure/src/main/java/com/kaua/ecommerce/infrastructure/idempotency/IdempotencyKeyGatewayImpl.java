package com.kaua.ecommerce.infrastructure.idempotency;

import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.exceptions.IdempotencyKeyAlreadyExistsException;
import com.kaua.ecommerce.infrastructure.exceptions.InternalServerErrorException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class IdempotencyKeyGatewayImpl implements IdempotencyKeyGateway {

    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";

    private final RedisTemplate<String, Object> redisTemplate;

    public IdempotencyKeyGatewayImpl(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
    }

    @Override
    public void save(String idempotencyKey, long ttl, TimeUnit timeUnit) {
        final var aKey = IDEMPOTENCY_KEY_PREFIX.concat(idempotencyKey);

        final var aTransactionResult = redisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                final var aOperation = (RedisOperations<String, Object>) operations;
                aOperation.watch(aKey);

                if (Boolean.TRUE.equals(aOperation.hasKey(aKey))) {
                    aOperation.unwatch();
                    return false;
                }

                aOperation.multi();

                aOperation.opsForHash().putIfAbsent(aKey, "body", "");
                aOperation.opsForHash().putIfAbsent(aKey, "headers", "");
                aOperation.opsForHash().putIfAbsent(aKey, "statusCode", "");

                return !aOperation.exec().isEmpty();
            }
        });

        if (!Boolean.TRUE.equals(aTransactionResult)) {
            throw new IdempotencyKeyAlreadyExistsException();
        }

        redisTemplate.expire(aKey, ttl, timeUnit);
    }

    @Override
    public void save(final String idempotencyKey, final Object body, final long ttl, final TimeUnit timeUnit) {
        final var aKey = IDEMPOTENCY_KEY_PREFIX.concat(idempotencyKey);

        if (body instanceof IdempotencyKeyInput input) {
            final var aTransactionResult = redisTemplate.execute(new SessionCallback<Boolean>() {
                @Override
                public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                    final var aOperation = (RedisOperations<String, Object>) operations;
                    aOperation.watch(aKey);

                    aOperation.multi();

                    // This serializations need attention in the future
                    aOperation.opsForHash().put(aKey, "body", input.getBody());
                    aOperation.opsForHash().put(aKey, "headers", Json.writeValueAsString(input.getHeaders()));
                    aOperation.opsForHash().put(aKey, "statusCode", input.getStatusCode());

                    return !aOperation.exec().isEmpty();
                }
            });

            if (!Boolean.TRUE.equals(aTransactionResult)) {
                throw new IdempotencyKeyAlreadyExistsException();
            }

            redisTemplate.expire(aKey, ttl, timeUnit);
        } else {
            throw InternalServerErrorException.with("Body must be an instance of IdempotencyKeyInput", null);
        }
    }

    @Override
    public Optional<IdempotencyKeyOutput> get(String idempotencyKey) {
        final var aKey = IDEMPOTENCY_KEY_PREFIX.concat(idempotencyKey);
        redisTemplate.watch(aKey);

        final var aResult = redisTemplate.opsForHash().entries(aKey);

        if (aResult.isEmpty()) {
            return Optional.empty();
        }

        // new TypeReference<Map<String, String>>() {} to solve aHeaders json read value warning
        final var aBody = (String) aResult.get("body");
        final Map<String, String> aHeaders = Json.readValue((String) aResult.get("headers"), Map.class);
        final var aStatusCode = (int) aResult.get("statusCode");

        final var aIdempotencyKeyBody = new IdempotencyKeyOutput(aStatusCode, aBody, aHeaders);

        return Optional.of(aIdempotencyKeyBody);
    }
}
