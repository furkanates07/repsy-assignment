package com.repsy.controller;

import com.repsy.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PostMapping("/{packageName}/{version}")
    public ResponseEntity<String> deployPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestParam("package") MultipartFile packageFile,
            @RequestParam("meta") MultipartFile metaFile) {

        try {
            packageService.deployPackage(packageName, version, packageFile, metaFile);
            return ResponseEntity.ok("Package deployed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to deploy package: " + e.getMessage());
        }
    }

    @GetMapping("/{packageName}/{version}/{fileName}")
    public ResponseEntity<Resource> downloadPackage(
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
            return ResponseEntity.status(404).build();
        }
    }
}
