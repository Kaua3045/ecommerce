package com.kaua.ecommerce.application.gateways.responses;

public record AddressResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf
) {
}
