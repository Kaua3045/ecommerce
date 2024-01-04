package com.kaua.ecommerce.infrastructure.service.local;

import com.kaua.ecommerce.domain.utils.Resource;
import com.kaua.ecommerce.infrastructure.service.StorageService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageServiceImpl implements StorageService {

    private final Map<String, Resource> storage;

    public InMemoryStorageServiceImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    public void clear() {
        this.storage.clear();
    }

    public Map<String, Resource> storage() {
        return storage;
    }

    @Override
    public void store(String aKey, Resource aResource) {
        this.storage.put(aKey, aResource);
    }

    @Override
    public void delete(String location) {
        this.storage.remove(location);
    }
}
