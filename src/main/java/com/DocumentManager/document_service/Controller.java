package com.DocumentManager.document_service;

import com.DocumentManager.document_service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class Controller {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload/{userId}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String userId) {
        String fileUrl = fileService.uploadFile(file, userId);
        return new ResponseEntity<>(fileUrl, HttpStatus.OK);
    }

    @GetMapping("/download/{userId}/{fileName}")
    public ResponseEntity<String> downloadFile(@PathVariable String userId, @PathVariable String fileName) {
        String fileUrl = fileService.downloadFile(fileName, userId);
        return new ResponseEntity<>(fileUrl, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId}/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String userId, @PathVariable String fileName) {
        String result = fileService.deleteFile(fileName, userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Método para subir múltiples archivos
    @PostMapping("/upload/all/{userId}")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files, @PathVariable String userId) {
        List<String> fileUrls = fileService.uploadMultipleFiles(files, userId);
        return new ResponseEntity<>(fileUrls, HttpStatus.OK);
    }

    @GetMapping("/download/all/{userId}")
    public ResponseEntity<List<String>> downloadAllFiles(@PathVariable String userId) {
        List<String> filePaths = fileService.downloadAllFiles(userId);
        return new ResponseEntity<>(filePaths, HttpStatus.OK);
    }

    @DeleteMapping("/delete/all/{userId}")
    public ResponseEntity<String> deleteAllFiles(@PathVariable String userId) {
        String result = fileService.deleteAllFiles(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
