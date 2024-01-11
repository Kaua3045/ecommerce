package com.kaua.ecommerce.infrastructure.service;

import com.kaua.ecommerce.domain.utils.Resource;

public interface StorageService {
    
    void store(String aKey, Resource aResource);

    void delete(String location);
}
