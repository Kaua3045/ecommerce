package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class ProductElasticsearchGateway implements SearchGateway<Product> {

    private final ProductElasticsearchRepository productElasticsearchRepository;

    public ProductElasticsearchGateway(final ProductElasticsearchRepository productElasticsearchRepository) {
        this.productElasticsearchRepository = Objects.requireNonNull(productElasticsearchRepository);
    }

    @Transactional
    @Override
    public Product save(Product aggregateRoot) {
        this.productElasticsearchRepository.save(ProductElasticsearchEntity.toEntity(aggregateRoot));
        return aggregateRoot;
    }

    @Override
    public Pagination<Product> findAll(SearchQuery aQuery) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Product> findById(String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Product> findByIdNested(String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        this.productElasticsearchRepository.deleteById(id);
    }
}
