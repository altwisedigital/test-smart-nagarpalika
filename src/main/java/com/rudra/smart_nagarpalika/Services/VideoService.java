package com.rudra.smart_nagarpalika.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class VideoService {

    private static final String UPLOAD_DIR_VID = "C:/Users/rudra/OneDrive/Desktop/Office_projects/uploads/citizen_video_uploads/";

    public String SaveVideo(MultipartFile videoFile) throws IOException {

        if (videoFile.isEmpty()){
            throw new IOException("Vide file is Empty");
        }

        String cleanName = Objects.requireNonNull(videoFile.getOriginalFilename());
        String fileName = LocalDateTime.now()+ "_" +  cleanName;

        Path path = Paths.get(UPLOAD_DIR_VID + fileName);

        Files.createDirectories(path.getParent());

        Files.copy(videoFile.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
        log.info("Image saved at: {}", path.toString());
        return  fileName;
    }
}
