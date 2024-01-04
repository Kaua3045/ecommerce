package com.kaua.ecommerce.infrastructure.exceptions;

import com.kaua.ecommerce.domain.exceptions.DomainException;

import java.util.Collections;

public class ImageSizeNotValidException extends DomainException {

    public ImageSizeNotValidException() {
        super("Maximum image size is 600kb", Collections.emptyList());
    }
}
