package com.kaua.ecommerce.domain.exceptions;

import java.util.Collections;

public class NotAcceptDuplicatedItemsException extends DomainException {

    private NotAcceptDuplicatedItemsException(String aMessage) {
        super(aMessage, Collections.emptyList());
    }

    public static NotAcceptDuplicatedItemsException with(final String aSku) {
        return new NotAcceptDuplicatedItemsException(
                String.format("The item with SKU %s is already in the order", aSku)
        );
    }
}
