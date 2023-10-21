package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfCommand;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.infrastructure.api.CustomerAPI;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerCpfInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomerController implements CustomerAPI {

    private final UpdateCustomerCpfUseCase updateCustomerCpfUseCase;

    public CustomerController(final UpdateCustomerCpfUseCase updateCustomerCpfUseCase) {
        this.updateCustomerCpfUseCase = updateCustomerCpfUseCase;
    }

    @Override
    public ResponseEntity<?> updateCustomerCpf(String accountId, UpdateCustomerCpfInput body) {
        final var aResult = this.updateCustomerCpfUseCase
                .execute(UpdateCustomerCpfCommand.with(accountId, body.cpf()));

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.OK).body(aResult.getRight());
    }
}
