package com.kaua.ecommerce.infrastructure.exceptions;

import com.kaua.ecommerce.domain.exceptions.DomainException;

import java.util.Collections;

public class ImageTypeNotValidException extends DomainException {

    public ImageTypeNotValidException() {
        super("Image type is not valid, types accept: jpg, jpeg and png", Collections.emptyList());
    }
}
