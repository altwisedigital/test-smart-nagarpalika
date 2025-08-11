package com.rudra.smart_nagarpalika.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlertsModel {
    @Id
    private int id;

     private  String description;

     private String title;

     private String imageUrl;

     private LocalDateTime createdAt;

}
