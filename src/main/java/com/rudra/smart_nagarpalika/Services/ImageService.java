package com.rudra.smart_nagarpalika.Services;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private String bucketName;

    @Value("${upload.citizen.dir}")
    private String citizenUploadDir;

    @Value("${upload.employee.dir}")
    private String employeeUploadDir;

    @Value("${upload.alert.dir}")
    private String alertUploadDir;

    @Value("${upload.category.dir}")
    private String categoryLogoDir;

    private final OkHttpClient httpClient = new OkHttpClient();

    private String uploadFileToSupabase(MultipartFile imageFile, String folder) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IOException("Image file is empty.");
        }

        // Generate clean filename
        String cleanName = Objects.requireNonNull(imageFile.getOriginalFilename())
                .replaceAll("\\s+", "_");
        String filename = folder + "/" + UUID.randomUUID() + "_" + cleanName;

        // Create request body
        RequestBody requestBody = RequestBody.create(
                imageFile.getBytes(),
                MediaType.parse(imageFile.getContentType())
        );

        // Build the request
        Request request = new Request.Builder()
                .url(supabaseUrl + "/storage/v1/object/" + bucketName + "/" + filename)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", imageFile.getContentType())
                .build();

        // Execute request
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                log.error("Failed to upload file to Supabase. Response: {} - {}", response.code(), errorBody);
                throw new IOException("Failed to upload file: " + response.code() + " - " + errorBody);
            }

            // Return the public URL
            String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + filename;
            log.info("File uploaded successfully: {}", publicUrl);
            return publicUrl;
        }
    }

    public String saveCitizenImage(MultipartFile imageFile) throws IOException {
        return uploadFileToSupabase(imageFile, citizenUploadDir);
    }

    public String saveEmployeeImage(MultipartFile imageFile) throws IOException {
        return uploadFileToSupabase(imageFile, employeeUploadDir);
    }

    public String saveAlertImage(MultipartFile imageFile) throws IOException {
        return uploadFileToSupabase(imageFile, alertUploadDir);
    }

    public String saveCategoryLogo(MultipartFile imageFile) throws IOException {
        return uploadFileToSupabase(imageFile, categoryLogoDir);
    }

    // Optional: Method to delete files
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract the path after "/public/"
            String publicPath = "/storage/v1/object/public/" + bucketName + "/";
            if (fileUrl.contains(publicPath)) {
                String filePath = fileUrl.substring(fileUrl.indexOf(publicPath) + publicPath.length());

                Request request = new Request.Builder()
                        .url(supabaseUrl + "/storage/v1/object/" + bucketName + "/" + filePath)
                        .delete()
                        .addHeader("Authorization", "Bearer " + supabaseKey)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    return response.isSuccessful();
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            return false;
        }
    }
}