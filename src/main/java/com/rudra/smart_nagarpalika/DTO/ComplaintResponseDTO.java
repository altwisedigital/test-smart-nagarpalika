package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Model.ImageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public ComplaintResponseDTO(ComplaintModel complaint) {
        this.id = complaint.getId();
        this.description = complaint.getDescription();
        this.category = complaint.getCategory();
        this.location = complaint.getLocation(); // or convert to String if needed


        this.imageUrls = complaint.getImages().stream()
                .map(ImageModel::getImageUrl)
                .collect(Collectors.toList());
        this.submittedBy = complaint.getSubmittedBy();
        this.status = complaint.getStatus().toString(); // if enum
//        this.assignedEmployeeName = complaint.getAssignedEmployee() != null
//                ? complaint.getAssignedEmployee().getName()
//                : null;
        this.createdAt = complaint.getCreatedAt();
    }
}
