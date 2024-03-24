package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.resource.Resource;
import com.kaua.ecommerce.infrastructure.exceptions.FileStorageException;
import com.kaua.ecommerce.infrastructure.service.StorageService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.Objects;

public class S3StorageServiceImpl implements StorageService {

    private final String bucketName;
    private final S3Client s3Client;

    public S3StorageServiceImpl(final String bucketName, final S3Client s3Client) {
        this.bucketName = Objects.requireNonNull(bucketName);
        this.s3Client = Objects.requireNonNull(s3Client);
    }

    @Override
    public void store(String aKey, Resource aResource) {
        try {
            final var aRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(aKey)
                    .contentType(aResource.contentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            this.s3Client.putObject(aRequest, RequestBody
                    .fromBytes(aResource.content()));
        } catch (final Exception e) {
            throw FileStorageException.with("Error on store file", e);
        }
    }

    @Override
    public void delete(String location) {
        try {
            final var aDeleteRequest = DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(location)
                    .build();

            this.s3Client.deleteObject(aDeleteRequest);
        } catch (final Exception e) {
            throw FileStorageException.with("Error on delete file", e);
        }
    }

    @Override
    public void deleteAllByLocation(List<String> locations) {
        try {
            if (locations.isEmpty()) {
                return;
            }

            final var aObjectsIdentifiers = locations.stream()
                    .map(location -> ObjectIdentifier.builder()
                            .key(location)
                            .build())
                    .toList();

            final var aDeleteRequest = DeleteObjectsRequest.builder()
                    .delete(builder -> builder.objects(aObjectsIdentifiers))
                    .bucket(bucketName)
                    .build();

            this.s3Client.deleteObjects(aDeleteRequest);
        } catch (Exception e) {
            throw FileStorageException.with("Error on delete files by locations", e);
        }
    }
}
