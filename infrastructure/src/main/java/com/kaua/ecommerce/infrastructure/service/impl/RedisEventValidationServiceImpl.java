package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.service.EventValidationService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

@Component
public class RedisEventValidationServiceImpl implements EventValidationService {

    public static final String EVENT_VALIDATION_PREFIX = "event-validation:";

    private final RedisTemplate<String, Long> redisTemplate;

    public RedisEventValidationServiceImpl(
            final RedisTemplate<String, Long> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    // TODO: aparentemente as transaction não funcionam, o certo seria usar operações atomicas
    // mas como vamos remover isso pois vamos adicionar uma version direto no db, não vamos nos preocupar com isso
    // o db e o elastic, manteram a version e logo quando recebermos um evento, vamos verificar se a version é a mesma
    // vamos verficar no elastic se a versão é a no futuro, se for, ignoramos o evento
    // no momento as operações estão atomicas, a concorrencia esta ok
    // mas não é garantido que vai funcionar em todos os casos
    // o certo seria usar talvez o dynamodb para garantir a atomicidade e a concorrencia
    // no dynamodb poderiamos muito bem salvar o evento (eventId (unico), aggregateId (id do produto por ex),
    // aggregateName, version (isso precisaria vir do db, ou pegarmos de alguma outra forma),
    // payload, occurredOn)
    @Override
    public <T extends DomainEvent> boolean isInvalid(final T event, final String payloadId) {
        final var aEventValidationId = event.aggregateName().concat("-").concat(payloadId);
        final var aEventValidationKey = EVENT_VALIDATION_PREFIX.concat(aEventValidationId);

        final var aSessionCallBack = new SessionCallback<Boolean>() {

            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                final var aRedisOperations = (RedisOperations<String, Long>) operations;

                aRedisOperations.watch(aEventValidationKey);

                final var aSavedOccurredOn = Boolean.TRUE.equals(aRedisOperations.hasKey(aEventValidationKey))
                        ? InstantUtils.fromTimestamp(aRedisOperations.opsForValue().get(aEventValidationKey))
                        : null;

                aRedisOperations.multi();

                if (aSavedOccurredOn == null || aSavedOccurredOn.isBefore(event.occurredOn())) {
                    aRedisOperations.opsForValue().set(aEventValidationKey, event.occurredOn().toEpochMilli());
                    aRedisOperations.exec();
                    return false;
                } else {
                    aRedisOperations.discard();
                    return true;
                }
            }
        };

        return Boolean.TRUE.equals(redisTemplate.execute(aSessionCallBack));
    }

    @Override
    public void invalidate(String aggregateName, String payloadId) {
        final var aEventValidationId = aggregateName.concat("-").concat(payloadId);
        final var aEventValidationKey = EVENT_VALIDATION_PREFIX.concat(aEventValidationId);
        this.redisTemplate.delete(aEventValidationKey);
    }
}
