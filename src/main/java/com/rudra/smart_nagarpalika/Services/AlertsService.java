package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.AlertRequestDto;
import com.rudra.smart_nagarpalika.DTO.AlertResponseDTO;
import com.rudra.smart_nagarpalika.Model.AlertsModel;
import com.rudra.smart_nagarpalika.Repository.AlertRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Service class for managing alerts in the Smart Nagarpalika application.
 * Provides methods for creating, retrieving, and deleting alerts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertsService {

    private final AlertRepo alertRepo;
    private final ImageService imageService;

    /**
     * Saves a new alert with an optional image.
     *
     * @param dto   The AlertRequestDto containing alert details.
     * @param image The optional image file to be uploaded with the alert.
     * @return The saved AlertsModel entity.
     * @throws IllegalArgumentException If the DTO or required fields are invalid.
     * @throws RuntimeException         If image upload fails or no valid image is provided.
     */
    public AlertsModel saveAlert(AlertRequestDto dto, MultipartFile image) {
        // Validate input DTO
        if (Objects.isNull(dto) || isInvalidDto(dto)) {
            log.error("Invalid AlertRequestDto provided: {}", dto);
            throw new IllegalArgumentException("Alert request data is invalid or missing required fields");
        }

        String uploadedImage = "";
        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
            try {
                uploadedImage = imageService.saveAlertImage(image);
                log.info("Image uploaded successfully: {}", uploadedImage);
            } catch (IOException e) {
                log.error("Failed to save image: {}", image.getOriginalFilename(), e);
                throw new RuntimeException("Failed to upload image: " + image.getOriginalFilename(), e);
            }
        } else {
            log.warn("No image provided for alert creation");
        }

        // Create and populate AlertsModel
        AlertsModel alert = new AlertsModel();
        alert.setTitle(dto.getTitle().trim());
        alert.setType(dto.getType().trim());
        alert.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : "");
        alert.setCreatedAt(LocalDateTime.now());
        alert.setImageUrl(uploadedImage);

        // Save alert to repository
        AlertsModel savedAlert = alertRepo.save(alert);
        log.info("Alert saved successfully with ID: {}", savedAlert.getId());
        return savedAlert;
    }

    /**
     * Validates the AlertRequestDto for required fields and content.
     *
     * @param dto The AlertRequestDto to validate.
     * @return True if the DTO is invalid, false otherwise.
     */
    private boolean isInvalidDto(AlertRequestDto dto) {
        return dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
                dto.getType() == null || dto.getType().trim().isEmpty();
    }

    /**
     * Retrieves an alert by its ID.
     *
     * @param id The ID of the alert to retrieve.
     * @return The AlertsModel entity if found.
     * @throws IllegalArgumentException If the ID is null.
     * @throws RuntimeException         If the alert is not found.
     */
    public AlertsModel getAlertById(Long id) {
        if (id == null) {
            log.error("Null ID provided for getAlertById");
            throw new IllegalArgumentException("Alert ID cannot be null");
        }

        return alertRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Alert not found for ID: {}", id);
                    return new RuntimeException("Alert not found with ID: " + id);
                });
    }

    /**
     * Retrieves all alerts and maps them to AlertResponseDTO.
     *
     * @return A list of AlertResponseDTOs representing all alerts.
     */
    public List<AlertResponseDTO> getAlerts() {
        List<AlertsModel> allAlerts = alertRepo.findAll();
        log.info("Retrieved {} alerts", allAlerts.size());
        return allAlerts.stream()
                .map(AlertResponseDTO::new)
                .toList();
    }

    /**
     * Retrieves all alerts as AlertsModel entities.
     *
     * @return A list of all AlertsModel entities.
     */
    public List<AlertsModel> getAllAlerts() {
        List<AlertsModel> allAlerts = alertRepo.findAll();
        log.info("Retrieved {} alerts as AlertsModel", allAlerts.size());
        return allAlerts;
    }

    /**
     * Deletes an alert by its ID.
     *
     * @param id The ID of the alert to delete.
     * @return True if the alert was deleted, false if not found.
     * @throws IllegalArgumentException If the ID is null.
     */
    public boolean deleteAlert(Long id) {
        if (id == null) {
            log.error("Null ID provided for deleteAlert");
            throw new IllegalArgumentException("Alert ID cannot be null");
        }

        if (alertRepo.existsById(id)) {
            alertRepo.deleteById(id);
            log.info("Alert deleted successfully with ID: {}", id);
            return true;
        }

        log.warn("Attempted to delete non-existent alert with ID: {}", id);
        return false;
    }
}