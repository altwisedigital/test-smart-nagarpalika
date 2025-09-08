package com.rudra.smart_nagarpalika.DTO;// CategoryRequest.java


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryRequest {
    private String name;
    private MultipartFile image; // optional, can be updated separately
}
