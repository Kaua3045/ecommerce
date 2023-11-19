package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.infrastructure.AbstractElasticsearchTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryElasticsearchGatewayTest extends AbstractElasticsearchTest {

    @Autowired
    private CategoryElasticsearchGateway categoryElasticsearchGateway;

    @Autowired
    private CategoryElasticsearchRepository categoryElasticsearchRepository;

    @Test
    void testInjection() {
        Assertions.assertNotNull(this.categoryElasticsearchGateway);
        Assertions.assertNotNull(this.categoryElasticsearchRepository);
    }

    @Test
    void givenAValidCategoryWithSubCategories_whenCallSave_shouldReturnCategorySaved() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory);
        aCategory.addSubCategories(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        Assertions.assertEquals(0, this.categoryElasticsearchRepository.count());

        final var actualCategory = this.categoryElasticsearchGateway.save(aCategory);

        Assertions.assertEquals(1, this.categoryElasticsearchRepository.count());

        Assertions.assertEquals(aCategory.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategory.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), actualCategory.getSlug());
        Assertions.assertTrue(actualCategory.getParentId().isEmpty());
        Assertions.assertEquals(1, actualCategory.getSubCategories().size());
        Assertions.assertEquals(1, actualCategory.getLevel());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
    }

    @Test
    void givenAValidCategoryWithoutSubCategories_whenCallSave_shouldReturnCategorySaved() {
        final var aCategory = Fixture.Categories.tech();

        Assertions.assertEquals(0, this.categoryElasticsearchRepository.count());

        final var actualCategory = this.categoryElasticsearchGateway.save(aCategory);

        Assertions.assertEquals(1, this.categoryElasticsearchRepository.count());

        Assertions.assertEquals(aCategory.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategory.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), actualCategory.getSlug());
        Assertions.assertTrue(actualCategory.getParentId().isEmpty());
        Assertions.assertEquals(0, actualCategory.getSubCategories().size());
        Assertions.assertEquals(0, actualCategory.getLevel());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
    }

    @Test
    void givenAValidCategoryWithNewName_whenCallSave_shouldReturnCategoryUpdated() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory);
        aCategory.addSubCategories(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity.toEntity(aCategory));

        Assertions.assertEquals(1, this.categoryElasticsearchRepository.count());

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();
        final var aCategoryUpdated = aCategory.update("New name", null, "new-name");

        final var actualCategory = this.categoryElasticsearchGateway.save(aCategoryUpdated);

        Assertions.assertEquals(1, this.categoryElasticsearchRepository.count());

        Assertions.assertEquals(aCategory.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategory.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), actualCategory.getSlug());
        Assertions.assertTrue(actualCategory.getParentId().isEmpty());
        Assertions.assertEquals(1, actualCategory.getSubCategories().size());
        Assertions.assertEquals(1, actualCategory.getLevel());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(aCategoryUpdatedAt.isBefore(actualCategory.getUpdatedAt()));
    }

    @Test
    void givenAValidCategoryId_whenCallDeleteById_shouldBeOk() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory);
        aCategory.addSubCategories(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity.toEntity(aCategory));

        Assertions.assertEquals(1, this.categoryElasticsearchRepository.count());

        Assertions.assertDoesNotThrow(() ->
                this.categoryElasticsearchGateway.deleteById(aCategory.getId().getValue()));

        Assertions.assertEquals(0, this.categoryElasticsearchRepository.count());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallDeleteById_shouldBeOk() {
        final var aCategoryId = "123";

        Assertions.assertEquals(0, this.categoryElasticsearchRepository.count());

        Assertions.assertDoesNotThrow(() ->
                this.categoryElasticsearchGateway.deleteById(aCategoryId));

        Assertions.assertEquals(0, this.categoryElasticsearchRepository.count());
    }

    @Test
    void givenEmptyCategories_whenCallFindAll_shouldReturnEmptyList() {
        final var aPage = 0;
        final var aPerPage = 10;
        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";
        final var aTotalItems = 0;
        final var aTotalPages = 0;

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);

        final var actualOutput = this.categoryElasticsearchGateway.findAll(aQuery);

        Assertions.assertEquals(aPage, actualOutput.currentPage());
        Assertions.assertEquals(aPerPage, actualOutput.perPage());
        Assertions.assertEquals(aTotalItems, actualOutput.totalItems());
        Assertions.assertEquals(aTotalPages, actualOutput.totalPages());
        Assertions.assertEquals(aTotalItems, actualOutput.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "tec,0,10,1,1,1,Tech",
            "ho,0,10,1,1,1,Home Cosmetics"
    })
    void givenAValidTerm_whenCallFindAll_shouldReturnElementsFiltered(
            final String aTerm,
            final int aPage,
            final int aPerPage,
            final int aTotalPages,
            final int aTotalItems,
            final int aTotalElements,
            final String aName
    ) {
        mockCategories();

        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerm, aSort, aDirection);

        final var actualOutput = this.categoryElasticsearchGateway.findAll(aQuery);

        System.out.println(actualOutput.items().get(0).getName());

        Assertions.assertEquals(aPage, actualOutput.currentPage());
        Assertions.assertEquals(aPerPage, actualOutput.perPage());
        Assertions.assertEquals(aTotalItems, actualOutput.totalItems());
        Assertions.assertEquals(aTotalPages, actualOutput.totalPages());
        Assertions.assertEquals(aTotalElements, actualOutput.items().size());
        Assertions.assertEquals(aName, actualOutput.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,3,3,1,Home Cosmetics",
            "name,desc,0,10,3,3,1,Tech",
            "created_at,asc,0,10,3,3,1,Tech",
            "created_at,desc,0,10,3,3,1,T-Shirt"
    })
    void givenAValidSortAndDirection_whenCallFindAll_shouldReturnElementsSorted(
            final String aSort,
            final String aDirection,
            final int aPage,
            final int aPerPage,
            final int aTotalItemsCount,
            final long aTotal,
            final int aTotalPages,
            final String aName
    ) {
        mockCategories();

        final var aTerms = "";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);

        final var actualOutput = this.categoryElasticsearchGateway.findAll(aQuery);

        System.out.println(actualOutput.items().get(0).getName());

        Assertions.assertEquals(aPage, actualOutput.currentPage());
        Assertions.assertEquals(aPerPage, actualOutput.perPage());
        Assertions.assertEquals(aTotalItemsCount, actualOutput.totalItems());
        Assertions.assertEquals(aTotal, actualOutput.items().size());
        Assertions.assertEquals(aTotalPages, actualOutput.totalPages());
        Assertions.assertEquals(aName, actualOutput.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,3,1,3,Home Cosmetics",
            "1,1,3,1,3,T-Shirt",
            "2,1,3,1,3,Tech"
    })
    void givenAValidPage_whenCallFindAll_shouldReturnElementsPaged(
            final int aPage,
            final int aPerPage,
            final int aTotalItemsCount,
            final long aTotal,
            final int aTotalPages,
            final String aName
    ) {
        mockCategories();

        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);

        final var actualOutput = this.categoryElasticsearchGateway.findAll(aQuery);

        System.out.println(actualOutput.items().get(0).getName());

        Assertions.assertEquals(aPage, actualOutput.currentPage());
        Assertions.assertEquals(aPerPage, actualOutput.perPage());
        Assertions.assertEquals(aTotalItemsCount, actualOutput.totalItems());
        Assertions.assertEquals(aTotal, actualOutput.items().size());
        Assertions.assertEquals(aTotalPages, actualOutput.totalPages());

        if (StringUtils.isNotEmpty(aName)) {
            Assertions.assertEquals(aName, actualOutput.items().get(0).getName());
        }
    }

    private void mockCategories() {
        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity
                .toEntity(Fixture.Categories.tech()));
        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity
                .toEntity(Fixture.Categories.home()));

        final var aTShirt = Category.newCategory(
                "T-Shirt",
                "T-Shirt",
                "t-shirt",
                null);
        final var aShirt = Category.newCategory(
                "Shirt",
                "Shirt",
                "shirt",
                aTShirt.getId());
        aTShirt.addSubCategory(aShirt);
        aTShirt.updateSubCategoriesLevel();

        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity.toEntity(aTShirt));
    }
}
