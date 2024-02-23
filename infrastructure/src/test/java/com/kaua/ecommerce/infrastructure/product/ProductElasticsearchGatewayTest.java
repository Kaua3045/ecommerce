package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.product.*;
import com.kaua.ecommerce.infrastructure.AbstractElasticsearchTest;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

public class ProductElasticsearchGatewayTest extends AbstractElasticsearchTest {

    @Autowired
    private ProductElasticsearchGateway productElasticsearchGateway;

    @Autowired
    private ProductElasticsearchRepository productElasticsearchRepository;

    @Test
    void givenAValidProduct_whenCallSave_shouldReturnProductSaved() {
        final var aProduct = Fixture.Products.tshirt();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        Assertions.assertEquals(0, this.productElasticsearchRepository.count());

        final var actualProduct = this.productElasticsearchGateway.save(aProduct);

        Assertions.assertEquals(1, this.productElasticsearchRepository.count());

        Assertions.assertEquals(aProduct.getId().getValue(), actualProduct.getId().getValue());
        Assertions.assertEquals(aProduct.getName(), actualProduct.getName());
        Assertions.assertEquals(aProduct.getDescription(), actualProduct.getDescription());
        Assertions.assertEquals(aProduct.getPrice(), actualProduct.getPrice());
        Assertions.assertEquals(aProduct.getBannerImage().get().getLocation(), actualProduct.getBannerImage().get().getLocation());
        Assertions.assertEquals(aProduct.getImages().size(), actualProduct.getImages().size());
        Assertions.assertEquals(aProduct.getCategoryId().getValue(), actualProduct.getCategoryId().getValue());
        Assertions.assertEquals(aProduct.getAttributes().size(), actualProduct.getAttributes().size());
        Assertions.assertEquals(aProduct.getStatus(), actualProduct.getStatus());
        Assertions.assertEquals(aProduct.getCreatedAt(), actualProduct.getCreatedAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), actualProduct.getUpdatedAt());
    }

    @Test
    void givenAValidProduct_whenCallDeleteById_shouldReturnProductSaved() {
        final var aProduct = Fixture.Products.book();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));
        this.productElasticsearchRepository.save(ProductElasticsearchEntity.toEntity(aProduct));

        Assertions.assertEquals(1, this.productElasticsearchRepository.count());

        Assertions.assertDoesNotThrow(() -> this.productElasticsearchGateway
                .deleteById(aProduct.getId().getValue()));

        Assertions.assertEquals(0, this.productElasticsearchRepository.count());
    }

    @Test
    void givenEmptyProducts_whenCallFindAll_shouldReturnEmptyList() {
        final var aPage = 0;
        final var aPerPage = 10;
        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";
        final var aTotalItems = 0;
        final var aTotalPages = 0;

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);

        final var actualOutput = this.productElasticsearchGateway.findAll(aQuery);

        Assertions.assertEquals(aPage, actualOutput.currentPage());
        Assertions.assertEquals(aPerPage, actualOutput.perPage());
        Assertions.assertEquals(aTotalItems, actualOutput.totalItems());
        Assertions.assertEquals(aTotalPages, actualOutput.totalPages());
        Assertions.assertEquals(aTotalItems, actualOutput.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "ca,0,10,1,1,1,Camiseta",
            "li,0,10,1,1,1,Livro",
            "he,0,10,1,1,1,Headphone"
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
        this.productElasticsearchRepository.save(ProductElasticsearchEntity
                .toEntity(Product.newProduct(
                        "Camiseta",
                        "Camiseta de algodão",
                        new BigDecimal("10.00"),
                        CategoryID.from("1"),
                        Set.of(Fixture.Products.productAttributes("Camiseta"))
                )));
        this.productElasticsearchRepository.save(ProductElasticsearchEntity
                .toEntity(Product.newProduct(
                        "Livro",
                        "Livro de teste",
                        new BigDecimal("100.00"),
                        CategoryID.from("1"),
                        Set.of(Fixture.Products.productAttributes("Livro"))
                )));
        this.productElasticsearchRepository.save(ProductElasticsearchEntity.toEntity(Product.newProduct(
                "Headphone",
                null,
                new BigDecimal("100.00"),
                CategoryID.from("1"),
                Set.of(ProductAttributes.create(
                        ProductColor.with("Black"),
                        ProductSize.with("G", 12.3, 10.0, 5.0, 0.5),
                        "Headphone"
                ))
        )));

        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerm, aSort, aDirection);

        final var actualOutput = this.productElasticsearchGateway.findAll(aQuery);

        Assertions.assertEquals(aPage, actualOutput.currentPage());
        Assertions.assertEquals(aPerPage, actualOutput.perPage());
        Assertions.assertEquals(aTotalItems, actualOutput.totalItems());
        Assertions.assertEquals(aTotalPages, actualOutput.totalPages());
        Assertions.assertEquals(aTotalElements, actualOutput.items().size());
        Assertions.assertEquals(aName, actualOutput.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,3,3,1,Camiseta",
            "name,desc,0,10,3,3,1,Livro",
            "created_at,asc,0,10,3,3,1,Camiseta",
            "created_at,desc,0,10,3,3,1,Headphone",
            "category_id,asc,0,10,3,3,1,Camiseta",
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
        this.productElasticsearchRepository.save(ProductElasticsearchEntity
                .toEntity(Product.newProduct(
                        "Camiseta",
                        "Camiseta de algodão",
                        new BigDecimal("10.00"),
                        CategoryID.from("1"),
                        Set.of(Fixture.Products.productAttributes("Camiseta"))
                )));
        this.productElasticsearchRepository.save(ProductElasticsearchEntity
                .toEntity(Product.newProduct(
                        "Livro",
                        "Livro de teste",
                        new BigDecimal("100.00"),
                        CategoryID.from("1"),
                        Set.of(Fixture.Products.productAttributes("Livro"))
                )));
        this.productElasticsearchRepository.save(ProductElasticsearchEntity.toEntity(Product.newProduct(
                "Headphone",
                null,
                new BigDecimal("100.00"),
                CategoryID.from("1"),
                Set.of(ProductAttributes.create(
                        ProductColor.with("Black"),
                        ProductSize.with("G", 12.3, 10.0, 5.0, 0.5),
                        "Headphone"
                ))
        )));

        final var aTerms = "";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);

        final var actualOutput = this.productElasticsearchGateway.findAll(aQuery);

        Assertions.assertEquals(aPage, actualOutput.currentPage());
        Assertions.assertEquals(aPerPage, actualOutput.perPage());
        Assertions.assertEquals(aTotalItemsCount, actualOutput.totalItems());
        Assertions.assertEquals(aTotal, actualOutput.items().size());
        Assertions.assertEquals(aTotalPages, actualOutput.totalPages());
        Assertions.assertEquals(aName, actualOutput.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,3,1,3,Camiseta",
            "1,1,3,1,3,Headphone",
            "2,1,3,1,3,Livro",
    })
    void givenAValidPage_whenCallFindAll_shouldReturnElementsPaged(
            final int aPage,
            final int aPerPage,
            final int aTotalItemsCount,
            final long aTotal,
            final int aTotalPages,
            final String aName
    ) {
        this.productElasticsearchRepository.save(ProductElasticsearchEntity
                .toEntity(Product.newProduct(
                        "Camiseta",
                        "Camiseta de algodão",
                        new BigDecimal("10.00"),
                        CategoryID.from("1"),
                        Set.of(Fixture.Products.productAttributes("Camiseta"))
                )));
        this.productElasticsearchRepository.save(ProductElasticsearchEntity
                .toEntity(Product.newProduct(
                        "Livro",
                        "Livro de teste",
                        new BigDecimal("100.00"),
                        CategoryID.from("1"),
                        Set.of(Fixture.Products.productAttributes("Livro"))
                )));
        this.productElasticsearchRepository.save(ProductElasticsearchEntity.toEntity(Product.newProduct(
                "Headphone",
                null,
                new BigDecimal("100.00"),
                CategoryID.from("1"),
                Set.of(ProductAttributes.create(
                        ProductColor.with("Black"),
                        ProductSize.with("G", 12.3, 10.0, 5.0, 0.5),
                        "Headphone"
                ))
        )));

        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);

        final var actualOutput = this.productElasticsearchGateway.findAll(aQuery);

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
    void testNotImplementedMethods() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> productElasticsearchGateway.findById(null));

        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> productElasticsearchGateway.findByIdNested(null));
    }
}
