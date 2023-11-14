package com.kaua.ecommerce.infrastructure.category;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface CategoryElasticRepository extends ElasticsearchRepository<CategoryEntityElastic, String> {

    Optional<CategoryEntityElastic> findByName(String name);
}
