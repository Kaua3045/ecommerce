package com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductElasticsearchEntityRepository extends ElasticsearchRepository<ProductElasticsearchEntity, String> {
}
