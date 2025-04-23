package com.repsy.storage.strategy.objectstorage;

import com.repsy.storage.strategy.StorageStrategy;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public class ObjectStorageStrategy implements StorageStrategy {

    private final MinioClient minioClient;
    private final String bucket;

    public ObjectStorageStrategy(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}") String secretKey,
            @Value("${minio.bucket}") String bucket
    ) {
        this.bucket = bucket;
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    public void saveFile(String packageName, String version, MultipartFile file, String fileName) throws Exception {
        String objectName = packageName + "/" + version + "/" + fileName;

        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());

        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        InputStream is = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(is, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
    }


    @Override
    public Resource loadFile(String packageName, String version, String fileName) throws Exception {
        String objectName = packageName + "/" + version + "/" + fileName;

        GetObjectResponse object = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );

        return new InputStreamResource(object);
    }
}
