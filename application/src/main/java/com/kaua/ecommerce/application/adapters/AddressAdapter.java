package com.kaua.ecommerce.application.adapters;

import com.kaua.ecommerce.application.adapters.responses.AddressResponse;

import java.util.Optional;

public interface AddressAdapter {

    Optional<AddressResponse> findAddressByZipCode(String zipCode);
}
