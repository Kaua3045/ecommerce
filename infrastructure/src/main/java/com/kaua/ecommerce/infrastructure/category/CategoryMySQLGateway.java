package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.events.CategoryDeletedEvent;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.service.EventDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private static final Logger log = LoggerFactory.getLogger(CategoryMySQLGateway.class);

    private final CategoryJpaEntityRepository categoryJpaEntityRepository;
    private final EventDatabaseService eventDatabaseService;

    public CategoryMySQLGateway(
            final CategoryJpaEntityRepository categoryJpaEntityRepository,
            final EventDatabaseService eventDatabaseService
    ) {
        this.categoryJpaEntityRepository = Objects.requireNonNull(categoryJpaEntityRepository);
        this.eventDatabaseService = Objects.requireNonNull(eventDatabaseService);
    }

    @Transactional
    @Override
    public Category create(Category aCategory) {
        this.categoryJpaEntityRepository.save(CategoryJpaEntity.toEntity(aCategory));
        aCategory.publishDomainEvent(this.eventDatabaseService::send);
        log.info("inserted category: {}", aCategory);
        return aCategory;
    }

    @Override
    public boolean existsByName(String aName) {
        return this.categoryJpaEntityRepository.existsByName(aName);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Category> findById(String aId) {
        return this.categoryJpaEntityRepository.findById(aId).map(CategoryJpaEntity::toDomain);
    }

    @Transactional
    @Override
    public Category update(Category aCategory) {
        this.categoryJpaEntityRepository.save(CategoryJpaEntity.toEntity(aCategory));
        aCategory.publishDomainEvent(this.eventDatabaseService::send);
        log.info("updated category: {}", aCategory);
        return aCategory;
    }

    @Override
    @Transactional
    public void deleteById(String aId) {
        if (this.categoryJpaEntityRepository.existsById(aId)) {
            this.categoryJpaEntityRepository.deleteById(aId);
            log.info("deleted category with id: {}", aId);
        }
    }

    @Transactional
    @Override
    public void deleteRootCategoryById(String aId) {
        if (this.categoryJpaEntityRepository.existsById(aId)) {
            publishRootCategoryDeletedEvent(aId);
            this.categoryJpaEntityRepository.deleteById(aId);
            log.info("deleted root category with id: {}", aId);
        }
    }

    private void publishRootCategoryDeletedEvent(final String aId) {
        final var aCategoryDeletedEvent = CategoryDeletedEvent.from(aId, null);
        this.eventDatabaseService.send(aCategoryDeletedEvent, null);
    }
}
