package com.kaua.ecommerce.domain.exceptions;

import java.util.Collections;

public class ItemOutOfStockException extends DomainException {

    public ItemOutOfStockException(final String aSku) {
        super("Item %s out of stock".formatted(aSku), Collections.emptyList());
    }
}
