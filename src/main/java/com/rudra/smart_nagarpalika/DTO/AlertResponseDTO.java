package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.AlertsModel;
import com.rudra.smart_nagarpalika.Services.IpServices;
import lombok.Data;

@Data
public class AlertResponseDTO {

    private Long id;
    private String imageUrl;
    private String description;
    private String title;
    private String type;


    public AlertResponseDTO(AlertsModel alerts) {
        this.id = alerts.getId();
        this.title = alerts.getTitle();
        this.description = alerts.getDescription();
        this.imageUrl = "http://" + IpServices.getCurrentIP() + ":8080"
                + "/uploads/Alert_image_uploads/" + alerts.getImageUrl();


        this.type = alerts.getType();
    }
}
