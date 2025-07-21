package com.rudra.smart_nagarpalika.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ComplaintRequestDTO {
//    private String title;
    private String description;
    private String category;
    private String location;
    private double latitude;
    private double longitude;
    private List<String> imageUrls ;

}
 