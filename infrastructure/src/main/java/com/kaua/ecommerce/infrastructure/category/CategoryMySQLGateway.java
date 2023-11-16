package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryMySQLGateway(final CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = Objects.requireNonNull(categoryJpaRepository);
    }

    @Override
    public Category create(Category aCategory) {
        this.categoryJpaRepository.save(CategoryJpaEntity.toEntity(aCategory));
        return aCategory;
    }

    @Override
    public boolean existsByName(String aName) {
        return this.categoryJpaRepository.existsByName(aName);
    }

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
        this.categoryJpaRepository.deleteById(aId);
    }
}
