package com.repsy.storage.strategy.filesystem;

import com.repsy.storage.strategy.StorageStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Component
public class FileSystemStorageStrategy implements StorageStrategy {

    @Value("${storage.root-path}")
    private Path root;

    @Override
    public void saveFile(String packageName, String version, MultipartFile file, String fileName) throws Exception {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            Path dir = root.resolve(packageName).resolve(version);
            Files.createDirectories(dir);

            Path filePath = dir.resolve(fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new Exception("Error occurred while saving the file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new Exception("File is empty: " + e.getMessage());
        }
    }

    @Override
    public Resource loadFile(String packageName, String version, String fileName) throws Exception {
        Path filePath = root.resolve(packageName).resolve(version).resolve(fileName);
        if (!Files.exists(filePath)) throw new RuntimeException("File not found");
        return new UrlResource(filePath.toUri());
    }
}
