package com.repsy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.config.StorageConfig;
import com.repsy.model.Package;
import com.repsy.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void deployPackage(String packageName, String version, MultipartFile packageFile, MultipartFile metaFile) throws Exception {
        try {
            if (packageName == null || packageName.isBlank()) {
                throw new IllegalArgumentException("Package name must not be empty.");
            }
            if (version == null || version.isBlank()) {
                throw new IllegalArgumentException("Version must not be empty.");
            }
            if (packageFile == null || packageFile.isEmpty()) {
                throw new IllegalArgumentException("Package file must not be empty.");
            }
            if (metaFile == null || metaFile.isEmpty()) {
                throw new IllegalArgumentException("Metadata file must not be empty.");
            }

            if (!packageFile.getOriginalFilename().endsWith(".rep")) {
                throw new IllegalArgumentException("Package file must be a .rep file.");
            }
            if (!metaFile.getOriginalFilename().endsWith(".json")) {
                throw new IllegalArgumentException("Metadata file must be a .json file.");
            }

            long maxFileSize = 50 * 1024 * 1024;
            if (packageFile.getSize() > maxFileSize) {
                throw new IllegalArgumentException("Package file is too large. Max allowed size is 50MB.");
            }

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

            if (packageRepository.existsByNameAndVersionAndStorageType(
                    pkg.getName(), pkg.getVersion(), pkg.getStorageType())) {
                throw new IllegalStateException("Package with this name, version, and storage type already exists.");
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
