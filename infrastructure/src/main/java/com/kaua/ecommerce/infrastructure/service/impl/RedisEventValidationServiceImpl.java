package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.infrastructure.outbox.event.EventValidationEntity;
import com.kaua.ecommerce.infrastructure.outbox.event.EventValidationRepository;
import com.kaua.ecommerce.infrastructure.service.EventValidationService;
import org.springframework.stereotype.Component;

@Component
public class RedisEventValidationServiceImpl implements EventValidationService {

    private final EventValidationRepository eventValidationRepository;

    public RedisEventValidationServiceImpl(
            final EventValidationRepository eventValidationRepository
    ) {
        this.eventValidationRepository = eventValidationRepository;
    }

    // TODO: aparentemente as transaction não funcionam, o certo seria usar operações atomicas
    // mas como vamos remover isso pois vamos adicionar uma version direto no db, não vamos nos preocupar com isso
    // o db e o elastic, manteram a version e logo quando recebermos um evento, vamos verificar se a version é a mesma
    // vamos verficar no elastic se a versão é a no futuro, se for, ignoramos o evento
    @Override
    public <T extends DomainEvent> boolean isInvalid(final T event, final String payloadId) {
        final var aEventValidationId = event.aggregateName().concat("-").concat(payloadId);

        final var aEventValidation = this.eventValidationRepository.findById(aEventValidationId);

        if (aEventValidation.isEmpty()) {
            final var aNewEventValidation = EventValidationEntity
                    .from(aEventValidationId, event.occurredOn());
            this.eventValidationRepository.save(aNewEventValidation);
            return false;
        }

        if (aEventValidation.get().getOccurredOn().isBefore(event.occurredOn())) {
            aEventValidation.get().setOccurredOn(event.occurredOn());
            this.eventValidationRepository.save(aEventValidation.get());
            return false;
        }

        return true;
    }

    @Override
    public void invalidate(String aggregateName, String payloadId) {
        final var aEventValidationId = aggregateName.concat("-").concat(payloadId);
        this.eventValidationRepository.deleteById(aEventValidationId);
    }
}
