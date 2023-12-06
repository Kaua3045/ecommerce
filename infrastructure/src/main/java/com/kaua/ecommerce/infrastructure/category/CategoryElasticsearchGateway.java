package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchRepository;
import org.apache.commons.lang3.StringUtils;
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
public class CategoryElasticsearchGateway implements SearchGateway<Category> {

    private static final String NAME_PROP = "name";
    private static final String KEYWORD = ".keyword";

    private final CategoryElasticsearchRepository categoryElasticsearchRepository;
    private final SearchOperations searchOperations;

    public CategoryElasticsearchGateway(
            final CategoryElasticsearchRepository categoryElasticsearchRepository,
            final SearchOperations searchOperations
    ) {
        this.categoryElasticsearchRepository = Objects.requireNonNull(categoryElasticsearchRepository);
        this.searchOperations = Objects.requireNonNull(searchOperations);
    }

    @Transactional
    @Override
    public Category save(Category aCategory) {
        this.categoryElasticsearchRepository.save(CategoryElasticsearchEntity.toEntity(aCategory));
        return aCategory;
    }

    @Override
    public Pagination<Category> findAll(SearchQuery aQuery) {
        final var aTerms = aQuery.terms();
        final var aCurrentPage = aQuery.page();
        final var aPerPage = aQuery.perPage();

        final var aSort = Sort.by(Sort.Direction.fromString(aQuery.direction()), buildSort(aQuery.sort()));
        final var aPage = PageRequest.of(aCurrentPage, aPerPage, aSort);

        final Query query;
        if (StringUtils.isNotEmpty(aTerms)) {
            final var aCriteria = Criteria.where("name").contains(aTerms)
                    .or("description").contains(aTerms);

            query = new CriteriaQuery(aCriteria, aPage);
        } else {
            query = Query.findAll().setPageable(aPage);
        }

        final var aResult = this.searchOperations.search(query, CategoryElasticsearchEntity.class);

        final var aTotal = aResult.getTotalHits();
        final var aTotalPages = (int) Math.ceil((double) aTotal / aPerPage);

        final var aCategories = aResult.stream()
                .map(SearchHit::getContent)
                .map(CategoryElasticsearchEntity::toDomain)
                .toList();

        return new Pagination<>(aCurrentPage, aPerPage, aTotalPages, aTotal, aCategories);
    }

    @Override
    public Optional<Category> findById(String id) {
        return this.categoryElasticsearchRepository.findById(id)
                .map(CategoryElasticsearchEntity::toDomain);
    }

    @Transactional
    @Override
    public void deleteById(String aId) {
        this.categoryElasticsearchRepository.deleteById(aId);
    }

    private String buildSort(final String sort) {
        if (NAME_PROP.equals(sort)) {
            return sort.concat(KEYWORD);
        } else {
            return sort;
        }
    }
}
