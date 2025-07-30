package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ComplaintRequestDTO {
//    private String title;
    private String description;
    private DepartmentModel department;
//    private String category;
    private String location;
    private double latitude;
    private double longitude;
    private List<String> imageUrls ;

}
 