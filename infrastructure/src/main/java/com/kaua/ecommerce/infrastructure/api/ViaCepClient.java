package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.application.adapters.responses.AddressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viaCepClient", url = "https://viacep.com.br/ws")
public interface ViaCepClient {

    @GetMapping("/{zipCode}/json")
    AddressResponse getAddressByZipCode(@PathVariable String zipCode);
}
