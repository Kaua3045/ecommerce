package com.kaua.ecommerce.application.exceptions;

import com.kaua.ecommerce.domain.exceptions.DomainException;

import java.util.Collections;

public class OnlyOneBannerImagePermittedException extends DomainException {

    public OnlyOneBannerImagePermittedException() {
        super("Only one banner image is allowed", Collections.emptyList());
    }
}
