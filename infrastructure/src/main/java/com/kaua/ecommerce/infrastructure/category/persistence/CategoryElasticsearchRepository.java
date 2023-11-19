package com.kaua.ecommerce.infrastructure.category.persistence;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryElasticsearchRepository extends ElasticsearchRepository<CategoryElasticsearchEntity, String> {
}
