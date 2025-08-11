
package com.rudra.smart_nagarpalika.Services;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    private static final String UPLOAD_DIR = "C:/Users/rudra/OneDrive/Desktop/Office_projects/uploads/";

    public String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IOException("Image file is empty.");
        }

        String cleanName = Objects.requireNonNull(imageFile.getOriginalFilename()).replaceAll("\\s+", "_");
        String filename = UUID.randomUUID() + "_" + cleanName;

        Path path = Paths.get(UPLOAD_DIR + filename);


        Files.createDirectories(path.getParent());

        Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        log.info("Image saved at: {}", path.toString());

        return  filename; // Return a relative path
    }
}
