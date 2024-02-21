package com.kaua.ecommerce.infrastructure.inventory.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommandParams;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CreateInventoryInput(
        @JsonProperty("product_id") String productId,
        @JsonProperty("attributes") List<CreateInventoryInputParams> inventoryParams
) {

    public Set<CreateInventoryCommandParams> toCommandInventoryParams() {
        return inventoryParams.stream()
                .map(it -> CreateInventoryCommandParams.with(it.sku(), it.quantity()))
                .collect(Collectors.toSet());
    }
}
