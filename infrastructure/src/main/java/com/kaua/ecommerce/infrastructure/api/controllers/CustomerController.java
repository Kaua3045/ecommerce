package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfCommand;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneCommand;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.infrastructure.api.CustomerAPI;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerCpfInput;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerTelephoneInput;
import com.kaua.ecommerce.infrastructure.customer.presenter.CustomerApiPresenter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController implements CustomerAPI {

    private final UpdateCustomerCpfUseCase updateCustomerCpfUseCase;
    private final UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase;
    private final GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase;

    public CustomerController(
            final UpdateCustomerCpfUseCase updateCustomerCpfUseCase,
            final UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase,
            final GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase
    ) {
        this.updateCustomerCpfUseCase = updateCustomerCpfUseCase;
        this.updateCustomerTelephoneUseCase = updateCustomerTelephoneUseCase;
        this.getCustomerByAccountIdUseCase = getCustomerByAccountIdUseCase;
    }

    @Override
    public ResponseEntity<?> getCustomer(String accountId) {
        return ResponseEntity.ok(CustomerApiPresenter.present(this.getCustomerByAccountIdUseCase
                .execute(accountId)));
    }

    @Override
    public ResponseEntity<?> updateCustomerCpf(String accountId, UpdateCustomerCpfInput body) {
        final var aResult = this.updateCustomerCpfUseCase
                .execute(UpdateCustomerCpfCommand.with(accountId, body.cpf()));

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.OK).body(aResult.getRight());
    }

    @Override
    public ResponseEntity<?> updateCustomerTelephone(String accountId, UpdateCustomerTelephoneInput body) {
        final var aResult = this.updateCustomerTelephoneUseCase
                .execute(UpdateCustomerTelephoneCommand.with(accountId, body.telephone()));

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.OK).body(aResult.getRight());
    }
}
