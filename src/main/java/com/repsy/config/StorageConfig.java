package com.repsy.config;

import com.repsy.storage.strategy.StorageStrategy;
import com.repsy.storage.strategy.filesystem.FileSystemStorageStrategy;
import com.repsy.storage.strategy.objectstorage.ObjectStorageStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Value("${storageStrategy}")
    private String storageStrategy;

    @Bean
    public StorageStrategy storageStrategyBean(FileSystemStorageStrategy fileSystem,
                                               ObjectStorageStrategy objectStorage) {
        System.out.println("here  " + storageStrategy);
        if ("object-storage".equalsIgnoreCase(storageStrategy)) {
            return objectStorage;
        } else if ("file-system".equalsIgnoreCase(storageStrategy)) {
            return fileSystem;
        } else {
            throw new IllegalArgumentException("Unsupported storage strategy: " + storageStrategy);
        }
    }

    public String getStorageStrategy() {
        return storageStrategy;
    }
}
