package com.kaua.ecommerce.infrastructure.inventory.presenter;

import com.kaua.ecommerce.application.usecases.inventory.retrieve.get.GetInventoryBySkuOutput;
import com.kaua.ecommerce.application.usecases.inventory.retrieve.list.ListInventoriesByProductIdOutput;
import com.kaua.ecommerce.infrastructure.inventory.models.GetInventoryResponse;
import com.kaua.ecommerce.infrastructure.inventory.models.ListInventoriesResponse;

public final class InventoryApiPresenter {

    private InventoryApiPresenter() {
    }

    public static ListInventoriesResponse present(final ListInventoriesByProductIdOutput aOutput) {
        return new ListInventoriesResponse(
                aOutput.id(),
                aOutput.productId(),
                aOutput.sku(),
                aOutput.quantity(),
                aOutput.createdAt(),
                aOutput.updatedAt()
        );
    }

    public static GetInventoryResponse present(final GetInventoryBySkuOutput aOutput) {
        return new GetInventoryResponse(
                aOutput.id(),
                aOutput.productId(),
                aOutput.sku(),
                aOutput.quantity(),
                aOutput.createdAt(),
                aOutput.updatedAt(),
                aOutput.version()
        );
    }
}
