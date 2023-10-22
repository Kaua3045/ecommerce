package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CpfUtils;
import com.kaua.ecommerce.domain.validation.Error;

public class Cpf extends ValueObject {

    private String cpf;

    private Cpf(final String aCpf) {
        this.cpf = CpfUtils.cleanCpf(aCpf);
        selfValidation();
    }

    public static Cpf newCpf(final String aCpf) {
        return new Cpf(aCpf);
    }

    private void selfValidation() {
        final var isValid = CpfUtils.validateCpf(cpf);
        if (!isValid) {
            throw DomainException.with(new Error("'cpf' invalid"));
        }
    }

    public String getCpf() {
        return cpf;
    }

    public String getFormattedCpf() {
        return CpfUtils.formatCpf(cpf);
    }
}
