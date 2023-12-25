package com.kaua.ecommerce.domain.utils;

import com.kaua.ecommerce.domain.ValueObject;

import java.io.InputStream;
import java.util.Objects;

public class Resource extends ValueObject {

    private final InputStream inputStream;
    private final String contentType;
    private final String fileName;

    private Resource(final InputStream inputStream, final String contentType, final String fileName) {
        this.inputStream = Objects.requireNonNull(inputStream);
        this.contentType = Objects.requireNonNull(contentType);
        this.fileName = Objects.requireNonNull(fileName);
    }

    public static Resource with(final InputStream inputStream, final String contentType, final String fileName) {
        return new Resource(inputStream, contentType, fileName);
    }

    public InputStream inputStream() {
        return inputStream;
    }

    public String contentType() {
        return contentType;
    }

    public String fileName() {
        return fileName;
    }
}
