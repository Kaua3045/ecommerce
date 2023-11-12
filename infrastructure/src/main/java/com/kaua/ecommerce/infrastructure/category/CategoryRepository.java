package com.kaua.ecommerce.infrastructure.category;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends Neo4jRepository<CategoryEntity, String> {

    @Query("MATCH path = (subCategories:categories {name: $name})<-[*]-(category) RETURN path")
    List<CategoryEntity> findPathToCategory(@Param("name") String name);

    Optional<CategoryEntity> findCategoryByName(String name);

    @Query("MATCH path=(root:categories)-[:HAS_SUBCATEGORY*0..]->(sub:categories) " +
            "WHERE NOT ()-[:HAS_SUBCATEGORY]->(root) " +
            "WITH root, collect(sub) as subcategories " +
            "RETURN root, subcategories")
    List<CategoryEntity> findCategoriesWithSubcategories();
}
