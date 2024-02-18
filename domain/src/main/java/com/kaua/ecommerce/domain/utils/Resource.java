package com.kaua.ecommerce.domain.utils;

import com.kaua.ecommerce.domain.ValueObject;

import java.util.Objects;

public class Resource extends ValueObject {

    private final byte[] content;
    private final String contentType;
    private final String fileName;

    private Resource(final byte[] content, final String contentType, final String fileName) {
        this.content = Objects.requireNonNull(content);
        this.contentType = Objects.requireNonNull(contentType);
        this.fileName = Objects.requireNonNull(fileName);
    }

    public static Resource with(final byte[] content, final String contentType, final String fileName) {
        final var aFilenameParts = fileName.replace(" ", "-").split("\\.");
        if (aFilenameParts[0].length() > 20) {
            aFilenameParts[0] = aFilenameParts[0].substring(0, 20);
        }
        final var aFilename = aFilenameParts[0] + "." + aFilenameParts[1];
        return new Resource(content, contentType, aFilename);
    }

    public byte[] content() {
        return content;
    }

    public String contentType() {
        return contentType;
    }

    public String fileName() {
        return fileName;
    }
}
