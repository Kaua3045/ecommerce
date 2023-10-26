package com.kaua.ecommerce.application.usecases.customer.update.cpf;

public record UpdateCustomerCpfCommand(String accountId, String cpf) {

    public static UpdateCustomerCpfCommand with(final String aAccountId, final String aCpf) {
        return new UpdateCustomerCpfCommand(aAccountId, aCpf);
    }
}
