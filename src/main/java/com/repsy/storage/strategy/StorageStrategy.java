package com.repsy.storage.strategy;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageStrategy {
    void saveFile(String packageName, String version, MultipartFile file, String fileName) throws Exception;
    Resource loadFile(String packageName, String version, String fileName) throws Exception;
}
