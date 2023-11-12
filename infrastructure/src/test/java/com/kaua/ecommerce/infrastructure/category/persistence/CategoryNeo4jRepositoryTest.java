package com.kaua.ecommerce.infrastructure.category.persistence;

import com.kaua.ecommerce.config.Neo4JTestConfiguration;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.Neo4jGatewayTest;
import com.kaua.ecommerce.infrastructure.category.CategoryEntity;
import com.kaua.ecommerce.infrastructure.category.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Neo4jGatewayTest
public class CategoryNeo4jRepositoryTest extends Neo4JTestConfiguration {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void givenAValidNullDescription_whenCallSave_shouldReturnAnException() {
        final var aCategory = new CategoryEntity(
                IdUtils.generate(),
                "Teste",
                "Teste",
                true,
                null,
                InstantUtils.now(),
                InstantUtils.now()
        );

        aCategory.setDescription(null);

        final var actualResult = Assertions.assertDoesNotThrow(() -> categoryRepository.save(aCategory));

        Assertions.assertEquals(aCategory.getId(), actualResult.getId());
        Assertions.assertEquals(aCategory.getName(), actualResult.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualResult.getDescription());
        Assertions.assertEquals(aCategory.isPrimary(), actualResult.isPrimary());
        Assertions.assertEquals(aCategory.getSubCategories(), actualResult.getSubCategories());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualResult.getUpdatedAt());
    }

    @Test
    void givenAValidIsPrimary_whenCallSave_shouldReturnAnException() {
        final var aCategory = new CategoryEntity(
                IdUtils.generate(),
                "Teste",
                "Teste",
                true,
                null,
                InstantUtils.now(),
                InstantUtils.now()
        );

        aCategory.setPrimary(false);

        final var actualException = Assertions.assertDoesNotThrow(() -> categoryRepository.save(aCategory));

        Assertions.assertEquals(aCategory.getId(), actualException.getId());
        Assertions.assertEquals(aCategory.getName(), actualException.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualException.getDescription());
        Assertions.assertFalse(actualException.isPrimary());
        Assertions.assertEquals(aCategory.getSubCategories(), actualException.getSubCategories());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualException.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualException.getUpdatedAt());
    }

    @Test
    void givenAEmptySubCategories_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "subCategories";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.category.persistence.CategoryEntity.subCategories";

        final var aCategory = new CategoryEntity(
                IdUtils.generate(),
                "Teste",
                "Teste",
                true,
                Set.of(
                        new CategoryEntity(
                                IdUtils.generate(),
                                "Teste",
                                "Teste",
                                true,
                                null,
                                InstantUtils.now(),
                                InstantUtils.now())),
                InstantUtils.now(),
                InstantUtils.now()
        );

        aCategory.setSubCategories(null);

        final var actualResult = Assertions.assertDoesNotThrow(() -> categoryRepository.save(aCategory));

        Assertions.assertEquals(aCategory.getId(), actualResult.getId());
        Assertions.assertEquals(aCategory.getName(), actualResult.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualResult.getDescription());
        Assertions.assertEquals(aCategory.isPrimary(), actualResult.isPrimary());
        Assertions.assertNull(actualResult.getSubCategories());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualResult.getUpdatedAt());
    }
}
