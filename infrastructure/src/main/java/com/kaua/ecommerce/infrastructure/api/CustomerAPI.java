package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerCpfInput;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "customers")
public interface CustomerAPI {

    @PatchMapping(
            value = "{accountId}/cpf",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> updateCustomerCpf(@PathVariable String accountId, @RequestBody UpdateCustomerCpfInput body);
}
