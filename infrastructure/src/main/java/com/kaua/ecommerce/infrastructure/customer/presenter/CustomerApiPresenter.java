package com.kaua.ecommerce.infrastructure.customer.presenter;

import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdOutput;
import com.kaua.ecommerce.infrastructure.customer.models.GetCustomerByAccountIdResponse;

public final class CustomerApiPresenter {

    private CustomerApiPresenter() {}

    public static GetCustomerByAccountIdResponse present(final GetCustomerByAccountIdOutput aOutput, final String aLocale) {
        return GetCustomerByAccountIdResponse.with(
                aOutput.id(),
                aOutput.accountId(),
                aOutput.firstName(),
                aOutput.lastName(),
                aOutput.email(),
                aOutput.cpf(),
                aOutput.telephone(),
                aOutput.address(),
                aOutput.createdAt(),
                aOutput.updatedAt(),
                aLocale
        );
    }
}
