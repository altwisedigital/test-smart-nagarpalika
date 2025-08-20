package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.AlertsModel;
import lombok.Data;

@Data
public class AlertResponseDTO {

    private Long id;
    private String imageUrl;
    private String description;
    private String title;


    public AlertResponseDTO(AlertsModel alerts) {
        this.id = alerts.getId();
        this.title = alerts.getTitle();
        this.description = alerts.getDescription();
        this.imageUrl = alerts.getImageUrl();
    }
}
