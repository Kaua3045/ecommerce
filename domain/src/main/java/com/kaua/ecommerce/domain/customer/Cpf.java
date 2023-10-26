package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CpfUtils;
import com.kaua.ecommerce.domain.validation.Error;

public class Cpf extends ValueObject {

    private String value;

    private Cpf(final String value) {
        this.value = CpfUtils.cleanCpf(value);
        selfValidation();
    }

    public static Cpf newCpf(final String aCpf) {
        return new Cpf(aCpf);
    }

    private void selfValidation() {
        final var isValid = CpfUtils.validateCpf(value);
        if (!isValid) {
            throw DomainException.with(new Error("'cpf' invalid"));
        }
    }

    public String getValue() {
        return value;
    }

    public String getFormattedCpf() {
        return CpfUtils.formatCpf(value);
    }
}
