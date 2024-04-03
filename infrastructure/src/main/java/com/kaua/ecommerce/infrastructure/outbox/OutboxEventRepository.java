package com.kaua.ecommerce.infrastructure.outbox;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, String> {

    // O skip locked só vai ser colocado se o banco suportar, por exemplo h2 não suporta
    // mas o mysql e postgresql suportam, então o hibernate vai colocar o skip locked
    @QueryHints({
            @QueryHint(
                    name = AvailableSettings.JAKARTA_LOCK_TIMEOUT,
                    value = "-2" // SKIP LOCKED
            )
    })
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<OutboxEventEntity> findTop50ByStatusOrderByOccurredOnAsc(final OutboxEventStatus status);
}
