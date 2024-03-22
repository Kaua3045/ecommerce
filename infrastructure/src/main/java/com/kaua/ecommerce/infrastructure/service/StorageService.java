package com.kaua.ecommerce.infrastructure.service;

import com.kaua.ecommerce.domain.resource.Resource;

import java.util.List;

public interface StorageService {
    
    void store(String aKey, Resource aResource);

    void delete(String location);

    void deleteAllByLocation(List<String> locations);
}
