package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchRepository;
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
public class CategoryElasticsearchGateway implements SearchGateway<Category> {

    private static final Logger log = LoggerFactory.getLogger(CategoryElasticsearchGateway.class);

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
        log.info("inserted category in elasticsearch: {}", aCategory);
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

    @Override
    public Optional<Category> findByIdNested(String id) {
        final var aCategory = this.categoryElasticsearchRepository.findById(id)
                .map(CategoryElasticsearchEntity::toDomain);

        if (aCategory.isEmpty()) {
            final var aSubCategory = this.findByIdInSubCategory(id);

            if (aSubCategory.isPresent()) {
                return aSubCategory;
            }

            return this.findByIdInSubSubCategory(id);
        }

        return aCategory;
    }

    @Transactional
    @Override
    public void deleteById(String aId) {
        this.categoryElasticsearchRepository.deleteById(aId);
        log.info("deleted category in elasticsearch with id: {}", aId);
    }

    private String buildSort(final String sort) {
        if (NAME_PROP.equals(sort)) {
            return sort.concat(KEYWORD);
        } else {
            return sort;
        }
    }

    public Optional<Category> findByIdInSubCategory(final String aId) {
        final var aResult = this.findCategoryByCriteria(Criteria.where("subCategories.id").is(aId));

        return aResult.flatMap(category -> category.getSubCategories().stream()
                .filter(aSubCategory -> aSubCategory.getId().getValue().equals(aId)).findFirst());
    }

    private Optional<Category> findByIdInSubSubCategory(final String aId) {
        final var aResult = this.findCategoryByCriteria(Criteria.where("subCategories.subCategories.id").is(aId));

        return aResult.flatMap(result -> result.getSubCategories().stream()
                .flatMap(aSubCategory -> aSubCategory.getSubCategories().stream())
                .filter(aSubSubCategory -> aSubSubCategory.getId().getValue().equals(aId)).findFirst());
    }

    private Optional<Category> findCategoryByCriteria(Criteria criteria) {
        CriteriaQuery query = new CriteriaQuery(criteria);
        Optional<CategoryElasticsearchEntity> result = this.searchOperations.search(query, CategoryElasticsearchEntity.class)
                .stream()
                .map(SearchHit::getContent)
                .findFirst();

        return result.map(CategoryElasticsearchEntity::toDomain);
    }
}
