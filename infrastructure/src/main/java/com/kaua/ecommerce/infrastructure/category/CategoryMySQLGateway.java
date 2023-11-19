package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import com.kaua.ecommerce.infrastructure.configurations.annotations.CategoryEvents;
import com.kaua.ecommerce.infrastructure.configurations.properties.kafka.KafkaTopicProperty;
import com.kaua.ecommerce.infrastructure.service.EventService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryJpaRepository categoryJpaRepository;
    private final EventService eventService;
    private final KafkaTopicProperty categoryTopic;

    public CategoryMySQLGateway(
            final CategoryJpaRepository categoryJpaRepository,
            final EventService eventService,
            final @CategoryEvents KafkaTopicProperty categoryTopic
    ) {
        this.categoryJpaRepository = Objects.requireNonNull(categoryJpaRepository);
        this.eventService = Objects.requireNonNull(eventService);
        this.categoryTopic = Objects.requireNonNull(categoryTopic);
    }

    @Transactional
    @Override
    public Category create(Category aCategory) {
        this.categoryJpaRepository.save(CategoryJpaEntity.toEntity(aCategory));
        aCategory.publishDomainEvent(this.eventService::send, categoryTopic.getTopicName());
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

    @Override
    public Category update(Category aCategory) {
        this.categoryJpaRepository.save(CategoryJpaEntity.toEntity(aCategory));
        return aCategory;
    }

    @Override
    @Transactional
    public void deleteById(String aId) {
        if (this.categoryJpaRepository.existsById(aId)) {
            this.categoryJpaRepository.deleteById(aId);
        }
    }
}
