package com.rudra.smart_nagarpalika.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AlertRequestDto {

    private MultipartFile image;
    private String title;
    private String description;
}
