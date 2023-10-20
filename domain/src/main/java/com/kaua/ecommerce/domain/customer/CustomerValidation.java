package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

import static com.kaua.ecommerce.domain.utils.CommonErrorMessage.lengthBetween;

public class CustomerValidation extends Validator {

    private final Customer customer;
    private static final int NAME_MINIMUM_LENGTH = 3;
    private static final int NAME_MAXIMUM_LENGTH = 255;

    protected CustomerValidation(final Customer customer, final ValidationHandler handler) {
        super(handler);
        this.customer = customer;
    }

    @Override
    public void validate() {
        checkAccountIdConstraints();
        checkFirstNameConstraints();
        checkLastNameConstraints();
        checkEmailConstraints();
    }

    private void checkAccountIdConstraints() {
        if (customer.getAccountId() == null || customer.getAccountId().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("accountId")));
        }
    }

    private void checkFirstNameConstraints() {
        if (customer.getFirstName() == null || customer.getFirstName().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("firstName")));
            return;
        }

        if (customer.getFirstName().trim().length() < NAME_MINIMUM_LENGTH ||
                customer.getFirstName().trim().length() > NAME_MAXIMUM_LENGTH) {
            this.validationHandler().append(new Error(lengthBetween("firstName", NAME_MINIMUM_LENGTH, NAME_MAXIMUM_LENGTH)));
        }
    }

    private void checkLastNameConstraints() {
        if (customer.getLastName() == null || customer.getLastName().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("lastName")));
            return;
        }

        if (customer.getLastName().trim().length() < NAME_MINIMUM_LENGTH ||
                customer.getLastName().trim().length() > NAME_MAXIMUM_LENGTH) {
            this.validationHandler().append(new Error(lengthBetween("lastName", NAME_MINIMUM_LENGTH, NAME_MAXIMUM_LENGTH)));
        }
    }

    private void checkEmailConstraints() {
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("email")));
        }
    }

    // TODO: check CPF constraints after add update method with cpf or changeCpf method
}
