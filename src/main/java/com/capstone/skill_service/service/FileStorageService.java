package com.capstone.skill_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final String UPLOAD_DIR = "uploads/";

    public String saveFile(MultipartFile file) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // create "uploads/" folder
        }

        String filePath = UPLOAD_DIR + UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        return filePath;
    }
}
