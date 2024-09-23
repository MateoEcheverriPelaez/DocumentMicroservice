package com.DocumentManager.document_service;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;

@Service
public class FileService {

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;
    @Value("${google.application.credentials}")
    private String credentialsPath;
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public String uploadFile(MultipartFile file, String userId) {
        try {
            String folderName = "usuarios/" + userId + "/";
            String fileName = folderName + file.getOriginalFilename();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();
            storage.create(blobInfo, file.getBytes());
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        } catch (IOException e) {
            throw new RuntimeException("Error subiendo archivo a GCP", e);
        }
    }

    public String downloadFile(String clientId, String fileName) {
        BlobId blobId = BlobId.of(bucketName, "clientes/" + clientId + "/" + fileName);
        Blob blob = storage.get(blobId);
        String tempFilePath = System.getProperty("java.io.tmpdir") + "/" + fileName;
        blob.downloadTo(Paths.get(tempFilePath));
        return tempFilePath;
    }

    public String deleteFile(String clientId, String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
        return "Archivo eliminado correctamente";
    }
}
