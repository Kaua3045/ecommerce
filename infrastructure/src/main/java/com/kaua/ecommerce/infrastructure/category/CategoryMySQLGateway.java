package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.events.CategoryDeletedEvent;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import com.kaua.ecommerce.infrastructure.service.EventDatabaseService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryJpaRepository categoryJpaRepository;
    private final EventDatabaseService eventDatabaseService;

    public CategoryMySQLGateway(
            final CategoryJpaRepository categoryJpaRepository,
            final EventDatabaseService eventDatabaseService
    ) {
        this.categoryJpaRepository = Objects.requireNonNull(categoryJpaRepository);
        this.eventDatabaseService = Objects.requireNonNull(eventDatabaseService);
    }

    @Transactional
    @Override
    public Category create(Category aCategory) {
        this.categoryJpaRepository.save(CategoryJpaEntity.toEntity(aCategory));
        aCategory.publishDomainEvent(this.eventDatabaseService::send);
        return aCategory;
    }

    @Override
    public boolean existsByName(String aName) {
        return this.categoryJpaRepository.existsByName(aName);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Category> findById(String aId) {
        return this.categoryJpaRepository.findById(aId).map(CategoryJpaEntity::toDomain);
    }

    @Transactional
    @Override
    public Category update(Category aCategory) {
        this.categoryJpaRepository.save(CategoryJpaEntity.toEntity(aCategory));
        aCategory.publishDomainEvent(this.eventDatabaseService::send);
        return aCategory;
    }

    @Override
    @Transactional
    public void deleteById(String aId) {
        if (this.categoryJpaRepository.existsById(aId)) {
            this.categoryJpaRepository.deleteById(aId);
        }
    }

    @Transactional
    @Override
    public void deleteRootCategoryById(String aId) {
        if (this.categoryJpaRepository.existsById(aId)) {
            publishRootCategoryDeletedEvent(aId);
            this.categoryJpaRepository.deleteById(aId);
        }
    }

    private void publishRootCategoryDeletedEvent(final String aId) {
        final var aCategoryDeletedEvent = CategoryDeletedEvent.from(aId, null);
        this.eventDatabaseService.send(aCategoryDeletedEvent, null);
    }
}
