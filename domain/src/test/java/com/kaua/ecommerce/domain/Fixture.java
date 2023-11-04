package com.kaua.ecommerce.domain;

import com.kaua.ecommerce.domain.customer.Cpf;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.Telephone;
import com.kaua.ecommerce.domain.customer.address.Address;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.IdUtils;

public final class Fixture {

    private Fixture() {}

    public static String notFoundMessage(final Class<? extends AggregateRoot<?>> anAggregate, final String id) {
        return NotFoundException.with(anAggregate, id).get().getMessage();
    }

    public static final class Customers {

        private static final String firstName = "Mans";
        private static final String lastName = "Frond";
        private static final String email = "mans.frond@tesss.com";

        public static final Customer customerDefault =
                Customer.newCustomer(
                        IdUtils.generate(),
                        firstName,
                        lastName,
                        email);

        public static final Customer customerWithCpf =
                Customer.newCustomer(
                                IdUtils.generate(),
                                firstName,
                                lastName,
                                email)
                        .changeCpf(Cpf.newCpf("815.959.150-01"));

        public static final Customer customerWithTelephoneAndCpf =
                Customer.newCustomer(
                                IdUtils.generate(),
                                firstName,
                                lastName,
                                email)
                        .changeTelephone(Telephone.newTelephone("+11234567890"))
                        .changeCpf(Cpf.newCpf("815.959.150-01"));

        public static final Customer customerWithAllParams =
                Customer.newCustomer(
                                IdUtils.generate(),
                                firstName,
                                lastName,
                                email)
                        .changeTelephone(Telephone.newTelephone("+15551234567"))
                        .changeCpf(Cpf.newCpf("815.959.150-01"))
                        .changeAddress(Address.newAddress(
                                "Rua Teste",
                                "270",
                                "apt 123",
                                "Bairro",
                                "Cidade",
                                "Estado",
                                "12345678"));
    }

    public static final class Addresses {

        public static final Address addressDefault = Address.newAddress(
                "Rua Teste",
                "123",
                "Complemento",
                "Bairro Teste",
                "Cidade Teste",
                "Estado Teste",
                "12345678"
        );
    }
}
