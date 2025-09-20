package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.AlertsModel;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertResponseDTO {

    private Long id;
    private String imageUrl;
    private String description;
    private String title;
    private String type;
    private LocalDateTime createdAt;


    public AlertResponseDTO(AlertsModel alerts) {
        this.id = alerts.getId();
        this.title = alerts.getTitle();
        this.description = alerts.getDescription();
        this.imageUrl =   alerts.getImageUrl();

        this.createdAt = alerts.getCreatedAt();
        this.type = alerts.getType();
    }
}
