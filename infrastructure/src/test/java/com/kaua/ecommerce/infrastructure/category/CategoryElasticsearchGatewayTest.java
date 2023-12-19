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

        Assertions.assertEquals(aPage, actualOutput.currentPage());
        Assertions.assertEquals(aPerPage, actualOutput.perPage());
        Assertions.assertEquals(aTotalItemsCount, actualOutput.totalItems());
        Assertions.assertEquals(aTotal, actualOutput.items().size());
        Assertions.assertEquals(aTotalPages, actualOutput.totalPages());

        if (StringUtils.isNotEmpty(aName)) {
            Assertions.assertEquals(aName, actualOutput.items().get(0).getName());
        }
    }

    @Test
    void givenAValidCategoryWithSubCategories_whenCallFindByIdInElasticsearch_shouldReturnACategory() {
        final var aCategoryRoot = Category.newCategory("Root", null, "root", null);
        final var aSubCategory = Category.newCategory("Sub", null, "sub", aCategoryRoot.getId());
        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();
        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity.toEntity(aCategoryRoot));

        Assertions.assertEquals(1, categoryElasticsearchRepository.count());

        final var actualCategory = this.categoryElasticsearchGateway.findById(aCategoryRoot.getId().getValue()).get();

        Assertions.assertEquals(aCategoryRoot.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategoryRoot.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategoryRoot.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategoryRoot.getSlug(), actualCategory.getSlug());
        Assertions.assertEquals(aCategoryRoot.getParentId(), actualCategory.getParentId());
        Assertions.assertEquals(aCategoryRoot.getLevel(), actualCategory.getLevel());
        Assertions.assertEquals(1, actualCategory.getSubCategories().size());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallFindByIdInElasticsearch_shouldReturnEmpty() {
        final var aId = "123";
        final var actualCategory = this.categoryElasticsearchGateway.findById(aId);
        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    void givenAValidRootCategoryWithSubCategories_whenCallFindByIdNestedInElasticsearch_shouldReturnACategory() {
        final var aCategoryRoot = Category.newCategory("Root", null, "root", null);
        categoryElasticsearchRepository.save(CategoryElasticsearchEntity.toEntity(aCategoryRoot));

        Assertions.assertEquals(1, categoryElasticsearchRepository.count());

        final var actualCategory = categoryElasticsearchGateway.findByIdNested(aCategoryRoot.getId().getValue()).get();

        Assertions.assertEquals(aCategoryRoot.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategoryRoot.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategoryRoot.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategoryRoot.getSlug(), actualCategory.getSlug());
        Assertions.assertEquals(aCategoryRoot.getParentId(), actualCategory.getParentId());
        Assertions.assertEquals(aCategoryRoot.getLevel(), actualCategory.getLevel());
        Assertions.assertEquals(0, actualCategory.getSubCategories().size());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
    }

    @Test
    void givenAValidSubCategoryIdWithoutSubCategories_whenCallFindByNestedInElasticsearch_shouldReturnACategory() {
        final var aCategoryRoot = Fixture.Categories.home();
        final var aSubCategory = Fixture.Categories.makeSubCategories(2, aCategoryRoot)
                .stream().findFirst().get();

        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();

        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity.toEntity(aCategoryRoot));

        Assertions.assertEquals(1, categoryElasticsearchRepository.count());

        final var actualCategory = this.categoryElasticsearchGateway.findByIdNested(aSubCategory.getId().getValue()).get();

        Assertions.assertEquals(aSubCategory.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aSubCategory.getName(), actualCategory.getName());
        Assertions.assertEquals(aSubCategory.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aSubCategory.getSlug(), actualCategory.getSlug());
        Assertions.assertEquals(aCategoryRoot.getId().getValue(), actualCategory.getParentId().get().getValue());
        Assertions.assertEquals(aSubCategory.getLevel(), actualCategory.getLevel());
        Assertions.assertEquals(0, actualCategory.getSubCategories().size());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
    }

    @Test
    void givenAValidSubSubCategoryId_whenCallFindByNestedInElasticsearch_shouldReturnACategory() {
        final var aCategoryRoot = Fixture.Categories.home();
        final var aSubCategory = Fixture.Categories.makeSubCategories(2, aCategoryRoot)
                .stream().findFirst().get();
        final var aSubSubCategory = Fixture.Categories.makeSubCategories(1, aSubCategory)
                .stream().findFirst().get();

        aSubCategory.addSubCategory(aSubSubCategory);
        aSubCategory.updateSubCategoriesLevel();

        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();

        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity.toEntity(aCategoryRoot));

        Assertions.assertEquals(1, categoryElasticsearchRepository.count());

        final var actualCategory = this.categoryElasticsearchGateway.findByIdNested(aSubSubCategory.getId().getValue()).get();

        Assertions.assertEquals(aSubSubCategory.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aSubSubCategory.getName(), actualCategory.getName());
        Assertions.assertEquals(aSubSubCategory.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aSubSubCategory.getSlug(), actualCategory.getSlug());
        Assertions.assertEquals(aSubCategory.getId().getValue(), actualCategory.getParentId().get().getValue());
        Assertions.assertEquals(aSubSubCategory.getLevel(), actualCategory.getLevel());
        Assertions.assertEquals(0, actualCategory.getSubCategories().size());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
    }

    @Test
    void givenAnInvalidSubCategoryId_whenCallFindByNestedInElasticsearch_shouldReturnEmpty() {
        final var aId = "123";
        final var actualCategory = this.categoryElasticsearchGateway.findByIdNested(aId);
        Assertions.assertTrue(actualCategory.isEmpty());
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
