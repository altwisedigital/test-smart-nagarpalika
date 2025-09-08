package com.rudra.smart_nagarpalika.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Value("${upload.citizen.dir}")
    private String citizenUploadDir;

    @Value("${upload.employee.dir}")
    private String employeeUploadDir;

    @Value("${upload.alert.dir}")
    private String alertUploadDir;

    @Value("${upload.category.dir}")
    private String categoryLogoDir;

    private String saveFile(MultipartFile imageFile, String baseDir) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IOException("Image file is empty.");
        }

        String cleanName = Objects.requireNonNull(imageFile.getOriginalFilename())
                .replaceAll("\\s+", "_");
        String filename = UUID.randomUUID() + "_" + cleanName;

        Path path = Paths.get(baseDir, filename);
        Files.createDirectories(path.getParent());
        Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        log.info("Image saved at: {}", path.toAbsolutePath());

        return filename; // or return path.toString() if you prefer
    }

    public String saveCitizenImage(MultipartFile imageFile) throws IOException {
        return saveFile(imageFile, citizenUploadDir);
    }

    public String saveEmployeeImage(MultipartFile imageFile) throws IOException {
        return saveFile(imageFile, employeeUploadDir);
    }

    public String saveAlertImage(MultipartFile imageFile) throws IOException {
        return saveFile(imageFile, alertUploadDir);
    }

    public String saveCategoryLogo(MultipartFile imageFile) throws IOException {
        return saveFile(imageFile, categoryLogoDir);
    }
}
