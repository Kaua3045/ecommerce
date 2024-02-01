package com.kaua.ecommerce.application.usecases.product.update.status;

public record UpdateProductStatusCommand(String id, String status) {

    public static UpdateProductStatusCommand with(String id, String status) {
        return new UpdateProductStatusCommand(id, status);
    }
}
