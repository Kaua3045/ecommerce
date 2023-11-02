package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressCommand;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfCommand;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneCommand;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.infrastructure.api.CustomerAPI;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerAddressInput;
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
    private final UpdateCustomerAddressUseCase updateCustomerAddressUseCase;

    public CustomerController(
            final UpdateCustomerCpfUseCase updateCustomerCpfUseCase,
            final UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase,
            final GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase,
            final UpdateCustomerAddressUseCase updateCustomerAddressUseCase
    ) {
        this.updateCustomerCpfUseCase = updateCustomerCpfUseCase;
        this.updateCustomerTelephoneUseCase = updateCustomerTelephoneUseCase;
        this.getCustomerByAccountIdUseCase = getCustomerByAccountIdUseCase;
        this.updateCustomerAddressUseCase = updateCustomerAddressUseCase;
    }

    @Override
    public ResponseEntity<?> getCustomer(String accountId, String locale) {
        return ResponseEntity.ok(CustomerApiPresenter.present(this.getCustomerByAccountIdUseCase
                .execute(accountId), locale));
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

    @Override
    public ResponseEntity<?> updateCustomerAddress(String accountId, UpdateCustomerAddressInput body) {
        final var aResult = this.updateCustomerAddressUseCase
                .execute(UpdateCustomerAddressCommand.with(
                        accountId,
                        body.street(),
                        body.number(),
                        body.complement(),
                        body.district(),
                        body.city(),
                        body.state(),
                        body.zipCode().replaceAll("[-.]", "")
                ));

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.OK).body(aResult.getRight());
    }
}
