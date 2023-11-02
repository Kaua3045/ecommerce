package com.kaua.ecommerce.infrastructure.adapters;

import com.kaua.ecommerce.application.adapters.AddressAdapter;
import com.kaua.ecommerce.application.adapters.responses.AddressResponse;
import com.kaua.ecommerce.infrastructure.api.ViaCepClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class AddressAdapterImpl implements AddressAdapter {

    private final ViaCepClient viaCepClient;

    @Autowired
    public AddressAdapterImpl(ViaCepClient viaCepClient) {
        this.viaCepClient = Objects.requireNonNull(viaCepClient);
    }

    @Override
    public Optional<AddressResponse> findAddressByZipCode(String zipCode) {
        final var viaCepResponse = this.viaCepClient.getAddressByZipCode(zipCode);
        return viaCepResponse.cep() == null
                ? Optional.empty()
                : Optional.of(viaCepResponse);
    }
}
