package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Model.DepartmentModel;
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
    private String departmentName; // maps to DepartmentModel
    private Long wardId;
    private String location;
    private List<String> imageUrls;
    private String submittedBy;
    private String status;
    private String assignedEmployeeName;
    private LocalDateTime createdAt;

    public ComplaintResponseDTO(ComplaintModel complaint) {
        this.id = complaint.getId();
        this.description = complaint.getDescription();

        // Handle null safety

        this.departmentName = complaint.getDepartment() != null
                ? complaint.getDepartment().getName()
                : null;


        this.wardId = complaint.getWard() != null
                ? complaint.getWard().getId()
                : null;

        this.location = complaint.getLocation();

        // Make sure images are null-safe
        this.imageUrls = complaint.getImages() != null
                ? complaint.getImages().stream()
                .map(ImageModel::getImageUrl)
                .collect(Collectors.toList())
                : List.of();

        this.submittedBy = complaint.getSubmittedBy();
        this.status = complaint.getStatus() != null
                ? complaint.getStatus().toString()
                : "UNKNOWN";

        this.assignedEmployeeName = complaint.getAssignedEmployee() != null
                ? complaint.getAssignedEmployee().getFirstname() + " " + complaint.getAssignedEmployee().getLastname()
                : null;

        this.createdAt = complaint.getCreatedAt();
    }

}
