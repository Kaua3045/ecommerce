package com.kaua.ecommerce.infrastructure.category.persistence;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryElasticsearchEntityRepository extends ElasticsearchRepository<CategoryElasticsearchEntity, String> {
}
