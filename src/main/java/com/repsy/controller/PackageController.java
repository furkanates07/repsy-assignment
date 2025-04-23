package com.repsy.controller;

import com.repsy.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PostMapping("/{packageName}/{version}")
    public ResponseEntity<Object> deployPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestParam("package") MultipartFile packageFile,
            @RequestParam("meta") MultipartFile metaFile) {

        try {
            String fileName = packageFile.getOriginalFilename();
            long fileSize = packageFile.getSize();

            packageService.deployPackage(packageName, version, packageFile, metaFile);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "Package deployed successfully.",
                    "packageName", packageName,
                    "version", version,
                    "fileName", fileName,
                    "fileSize", fileSize
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Failed to deploy package: " + e.getMessage(),
                    "packageName", packageName,
                    "version", version
            ));
        }
    }

    @GetMapping("/{packageName}/{version}/{fileName}")
    public ResponseEntity<Object> downloadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @PathVariable String fileName) {

        try {
            Resource fileResource = packageService.downloadPackage(packageName, version, fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(fileResource);

        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "File not found",
                    "packageName", packageName,
                    "version", version,
                    "fileName", fileName
            ));
        }
    }
}
