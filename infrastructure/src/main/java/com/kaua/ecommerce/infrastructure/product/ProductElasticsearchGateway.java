package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchEntity;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchEntityRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class ProductElasticsearchGateway implements SearchGateway<Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductElasticsearchGateway.class);

    private static final String NAME_PROP = "name";
    private static final String CATEGORY_ID_PROP = "category_id";
    private static final String KEYWORD = ".keyword";

    private final ProductElasticsearchEntityRepository productElasticsearchEntityRepository;
    private final SearchOperations searchOperations;

    public ProductElasticsearchGateway(
            final ProductElasticsearchEntityRepository productElasticsearchEntityRepository,
            final SearchOperations searchOperations
    ) {
        this.productElasticsearchEntityRepository = Objects.requireNonNull(productElasticsearchEntityRepository);
        this.searchOperations = Objects.requireNonNull(searchOperations);
    }

    @Transactional
    @Override
    public Product save(Product aggregateRoot) {
        this.productElasticsearchEntityRepository.save(ProductElasticsearchEntity.toEntity(aggregateRoot));
        log.info("inserted or updated product in elasticsearch: {}", aggregateRoot);
        return aggregateRoot;
    }

    @Override
    public Pagination<Product> findAll(SearchQuery aQuery) {
        final var aTerms = aQuery.terms();
        final var aCurrentPage = aQuery.page();
        final var aPerPage = aQuery.perPage();

        final var aSort = Sort.by(Sort.Direction.fromString(aQuery.direction()), buildSort(aQuery.sort()));
        final var aPage = PageRequest.of(aCurrentPage, aPerPage, aSort);

        final Query query;
        if (StringUtils.isNotEmpty(aTerms)) {
            final var aCriteria = Criteria.where(NAME_PROP).contains(aTerms)
                    .or(CATEGORY_ID_PROP).is(aTerms)
                    .or("description").contains(aTerms);

            query = new CriteriaQuery(aCriteria, aPage);
        } else {
            query = Query.findAll().setPageable(aPage);
        }

        final var aResult = this.searchOperations.search(query, ProductElasticsearchEntity.class);

        final var aTotal = aResult.getTotalHits();
        final var aTotalPages = (int) Math.ceil((double) aTotal / aPerPage);

        final var aProducts = aResult.stream()
                .map(SearchHit::getContent)
                .map(ProductElasticsearchEntity::toDomain)
                .toList();

        return new Pagination<>(aCurrentPage, aPerPage, aTotalPages, aTotal, aProducts);
    }

    @Override
    public Optional<Product> findById(String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Product> findByIdNested(String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        this.productElasticsearchEntityRepository.deleteById(id);
        log.info("deleted product from elasticsearch: {}", id);
    }

    private String buildSort(final String aSort) {
        if (aSort.equals(NAME_PROP) || aSort.equals(CATEGORY_ID_PROP)) {
            return aSort.concat(KEYWORD);
        }
        return aSort;
    }
}
