package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.infrastructure.service.EventStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
// Implementação do EventPublisher, aqui podemos publicar em mais de um lugar
// Fila, DB, no momento usamos um interface event storage service
// ela publica em algum db, não sabemos em qual db, mas sabemos que é um db
// o event storage service pode ter N implementações, uma para cada db
// Temos o KafkaEventProducer, ele publica no Kafka
// ele só é chamado quando o job é executado, ele pega os eventos do outbox
// então o fluxo fica assim
// registra o evento do usecase, chama o event publisher (cai aqui)
// o event publisher chama o event storage service, que salva no db
// o job pega os eventos do db e publica no Kafka chamando o event producer service
// se quiser não precisamos publicar no db, podemos chamar direto uma fila (dentro da mesma transaction)
// mas eu prefiro salvar no db, garantindo consistencia, se a fila falhar, o evento fica no db
// se falhar alguma coisa dentro da transaction, o evento n é publicado e também tudo que foi feito é desfeito
// assim evitamos publicar o evento mas ele ter falhado na transaction, ou então perder o evento
public class EventPublisherImpl implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisherImpl.class);

    private final EventStorageService eventStorageService;

    public EventPublisherImpl(EventStorageService eventStorageService) {
        this.eventStorageService = Objects.requireNonNull(eventStorageService);
    }

    @Override
    public <T extends DomainEvent> void publish(T event) {
        try {
            log.info("Event published: {}", event.getClass().getSimpleName());
            this.eventStorageService.send(event);
        } catch (final Exception e) {
            log.error("Error to publish event", e);
        }
    }
}
