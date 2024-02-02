package com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductElasticsearchRepository extends ElasticsearchRepository<ProductElasticsearchEntity, String> {
}
