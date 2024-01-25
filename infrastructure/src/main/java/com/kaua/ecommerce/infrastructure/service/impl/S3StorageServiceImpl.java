package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.utils.Resource;
import com.kaua.ecommerce.infrastructure.service.StorageService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
