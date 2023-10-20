package com.kaua.ecommerce.domain;

import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.Validation;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class TestValidationHandler implements ValidationHandler {

    private final List<Error> errors;

    public TestValidationHandler() {
        this.errors = new ArrayList<>();
    }

    @Override
    public ValidationHandler append(Error anError) {
        errors.add(anError);
        return this;
    }

    @Override
    public ValidationHandler append(ValidationHandler aHandler) {
        errors.addAll(aHandler.getErrors());
        return this;
    }

    @Override
    public ValidationHandler validate(Validation aValidation) {
        aValidation.validate();
        return null;
    }


    @Override
    public List<Error> getErrors() {
        return errors;
    }
}
