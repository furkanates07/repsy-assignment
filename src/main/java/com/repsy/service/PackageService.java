package com.repsy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.config.StorageConfig;
import com.repsy.model.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageConfig storageConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void deployPackage(String packageName, String version, MultipartFile packageFile, MultipartFile metaFile) throws Exception {
        try {
            Package pkg;
            try (InputStream metaStream = metaFile.getInputStream()) {
                pkg = objectMapper.readValue(metaStream, Package.class);
                pkg.setName(packageName);
                pkg.setVersion(version);

                if (pkg.getAuthor() == null || pkg.getAuthor().isBlank()) {
                    pkg.setAuthor("Unknown");
                }

                pkg.setSize(packageFile.getSize());
                pkg.setStorageType(storageConfig.getStorageStrategy());
                pkg.setCreatedAt(LocalDateTime.now());
                pkg.setUpdatedAt(pkg.getCreatedAt());
            }

            storageService.save(packageName, version, packageFile, packageName + ".rep");
            storageService.save(packageName, version, metaFile, "meta.json");

            packageRepository.save(pkg);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Package deploy failed: " + e.getMessage(), e);
        }
    }



    public Resource downloadPackage(String packageName, String version, String fileName) throws Exception {
        return storageService.load(packageName, version, fileName);
    }
}
