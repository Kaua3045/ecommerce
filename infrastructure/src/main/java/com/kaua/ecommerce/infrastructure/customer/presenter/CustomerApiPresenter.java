package com.kaua.ecommerce.infrastructure.customer.presenter;

import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdOutput;
import com.kaua.ecommerce.infrastructure.customer.models.GetCustomerByAccountIdResponse;

public final class CustomerApiPresenter {

    private CustomerApiPresenter() {}

    public static GetCustomerByAccountIdResponse present(final GetCustomerByAccountIdOutput aOutput) {
        return new GetCustomerByAccountIdResponse(
                aOutput.id(),
                aOutput.accountId(),
                aOutput.firstName(),
                aOutput.lastName(),
                aOutput.email(),
                aOutput.cpf(),
                aOutput.telephone(),
                aOutput.createdAt().toString(),
                aOutput.updatedAt().toString()
        );
    }
}
