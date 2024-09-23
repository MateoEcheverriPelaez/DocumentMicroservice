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
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private final KafkaProducer kafkaProducer;

    public FileService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public String uploadFile(MultipartFile file, String userId) {
        try {
            String folderName = "usuarios/" + userId + "/";
            String fileName = folderName + file.getOriginalFilename();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();
            storage.create(blobInfo, file.getBytes());

            // Enviar notificación de subida
            kafkaProducer.sendNotification("Archivo subido: " + fileName);
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        } catch (IOException e) {
            throw new RuntimeException("Error subiendo archivo a GCP", e);
        }
    }

    public String downloadFile(String clientId, String fileName) {
        try {
            BlobId blobId = BlobId.of(bucketName, "clientes/" + clientId + "/" + fileName);
            Blob blob = storage.get(blobId);
            String tempFilePath = System.getProperty("java.io.tmpdir") + "/" + fileName;
            blob.downloadTo(Paths.get(tempFilePath));

            // Enviar notificación de descarga
            kafkaProducer.sendNotification("Archivo descargado: " + fileName);
            return tempFilePath;
        } catch (Exception e) {
            throw new RuntimeException("Error descargando archivo de GCP", e);
        }
    }

    public String deleteFile(String clientId, String fileName) {
        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            storage.delete(blobId);

            // Enviar notificación de eliminación
            kafkaProducer.sendNotification("Archivo eliminado: " + fileName);
            return "Archivo eliminado correctamente";
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando archivo de GCP", e);
        }
    }
}
