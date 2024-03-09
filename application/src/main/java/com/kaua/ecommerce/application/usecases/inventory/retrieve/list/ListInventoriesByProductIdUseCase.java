package com.kaua.ecommerce.application.usecases.inventory.retrieve.list;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.domain.pagination.Pagination;

public abstract class ListInventoriesByProductIdUseCase extends
        UseCase<Pagination<ListInventoriesByProductIdOutput>, ListInventoriesByProductIdCommand> {
}
