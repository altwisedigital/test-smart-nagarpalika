package com.rudra.smart_nagarpalika.Services;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
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

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket.name}")
    private String bucket;

    @Value("${upload.citizen.dir}")
    private String citizenUploadDir;

    @Value("${upload.employee.dir}")
    private String employeeUploadDir;

    @Value("${upload.alert.dir}")
    private String alertUploadDir;

    @Value("${upload.category.dir}")
    private String categoryLogoDir;

    private final OkHttpClient client = new OkHttpClient();

    private String saveFile(MultipartFile imageFile, String folder) throws IOException {
        if (imageFile.isEmpty()) {
            log.error("Image file is empty: {}", imageFile.getOriginalFilename());
            throw new IOException("Image file is empty.");
        }

        // Generate clean unique filename
        String cleanName = Objects.requireNonNull(imageFile.getOriginalFilename())
                .replaceAll("[^a-zA-Z0-9.-]", "_");
        String filename = UUID.randomUUID() + "_" + cleanName;
        String path = folder + "/" + filename;
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + path;

        log.info("Uploading to Supabase: {}", uploadUrl);

        // Set Content-Type, fallback if null
        String contentType = imageFile.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
            log.warn("Null Content-Type for {}; using fallback", imageFile.getOriginalFilename());
        }

        // Prepare request body
        RequestBody requestBody = RequestBody.create(imageFile.getBytes(), MediaType.parse(contentType));

        // Build request
        Request request = new Request.Builder()
                .url(uploadUrl)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("x-upsert", "true") // Overwrite if exists
                .post(requestBody)
                .build();

        // Execute upload
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "No response body";
            log.info("Supabase response for {}: Code={}, Body={}", path, response.code(), responseBody);

            if (response.isSuccessful()) {
                String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + path;
                log.info("Image uploaded successfully to {}: {}", path, publicUrl);
                return publicUrl;
            } else {
                log.error("Upload failed for {}: {} - {}", path, response.code(), responseBody);
                throw new IOException("Upload failed: " + response.code() + " - " + responseBody);
            }
        } catch (IOException e) {
            log.error("Error uploading to {}: {}", path, e.getMessage());
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        }
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
