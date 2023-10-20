package com.kaua.ecommerce.application.usecases.customer.create;

public record CreateCustomerOutput(String id) {

    public static CreateCustomerOutput from(String id) {
        return new CreateCustomerOutput(id);
    }
}
