package com.rudra.smart_nagarpalika.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ComplaintResponseDTO {
    private Long id;
    private String description;
    private String category;
    private String location;
    private List<String> imageUrls;
    private String submittedBy;
    private String status;
    private String assignedEmployeeName;
    private LocalDateTime createdAt;
}
