package com.kaua.ecommerce.infrastructure.api;

import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerCpfInput;
import com.kaua.ecommerce.infrastructure.customer.models.UpdateCustomerTelephoneInput;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "customers")
public interface CustomerAPI {

    @GetMapping(
            value = "{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> getCustomer(@PathVariable String accountId);

    @PatchMapping(
            value = "{accountId}/cpf",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> updateCustomerCpf(@PathVariable String accountId, @RequestBody UpdateCustomerCpfInput body);

    @PatchMapping(
            value = "{accountId}/telephone",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> updateCustomerTelephone(@PathVariable String accountId, @RequestBody UpdateCustomerTelephoneInput body);
}
