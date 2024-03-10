package com.kaua.ecommerce.application.usecases.coupon.retrieve.list;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;

public abstract class ListCouponsUseCase extends UseCase<Pagination<ListCouponsOutput>, SearchQuery> {
}
