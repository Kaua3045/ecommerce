package com.kaua.ecommerce.infrastructure.customer.address;

import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.application.gateways.responses.AddressResponse;
import com.kaua.ecommerce.infrastructure.api.ViaCepClient;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class AddressViaCepGateway implements AddressGateway {

    private final ViaCepClient viaCepClient;

    public AddressViaCepGateway(final ViaCepClient viaCepClient) {
        this.viaCepClient = Objects.requireNonNull(viaCepClient);
    }

    @Override
    public Optional<AddressResponse> findAddressByZipCodeInExternalService(String zipCode) {
        final var viaCepResponse = this.viaCepClient.getAddressByZipCode(zipCode);
        return viaCepResponse.cep() == null
                ? Optional.empty()
                : Optional.of(viaCepResponse);
    }
}
