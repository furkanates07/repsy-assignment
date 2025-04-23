package com.repsy.service;

import com.repsy.storage.strategy.StorageStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    private final StorageStrategy strategy;

    public StorageService(@Qualifier("storageStrategyBean") StorageStrategy strategy) {
        this.strategy = strategy;
    }


    public void save(String packageName, String version, MultipartFile file, String fileName) throws Exception {
        strategy.saveFile(packageName, version, file, fileName);
    }

    public Resource load(String packageName, String version, String fileName) throws Exception {
        return strategy.loadFile(packageName, version, fileName);
    }
}
