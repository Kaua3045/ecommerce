package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.application.gateways.responses.AddressResponse;

import java.util.Optional;

public interface AddressGateway {

    Optional<AddressResponse> findAddressByZipCodeInExternalService(String zipCode);
}
