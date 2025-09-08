package com.rudra.smart_nagarpalika.DTO;// LocationRequest.java
import lombok.Data;

@Data
public class LocationRequest {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long categoryId; // reference to Category
}
