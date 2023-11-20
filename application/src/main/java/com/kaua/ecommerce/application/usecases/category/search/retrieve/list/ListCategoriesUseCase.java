package com.kaua.ecommerce.application.usecases.category.search.retrieve.list;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;

public abstract class ListCategoriesUseCase extends UseCase<Pagination<ListCategoriesOutput>, SearchQuery> {
}
