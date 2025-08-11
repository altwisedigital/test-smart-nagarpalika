package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Services.IpServices;
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
    private String wardName;
    private String location;
    private List<String> imageUrls;
    private String submittedBy;
    private String status;
    private String assignedEmployeeName;
    private LocalDateTime createdAt;


    public ComplaintResponseDTO(ComplaintModel complaint) {


        this.id = complaint.getId();
        this.description = complaint.getDescription();
        this.departmentName = complaint.getDepartment() != null ? complaint.getDepartment().getName() : null;
        this.wardName = complaint.getWard() != null ? complaint.getWard().getName() : null;
        this.location = complaint.getLocation();
        this.imageUrls = complaint.getImages() != null
                ? complaint.getImages().stream().map(imageModel -> ""+imageModel.getImageUrl()).toList()
                : List.of();
        this.submittedBy = complaint.getSubmittedBy();
        this.status = complaint.getStatus().name();
        this.assignedEmployeeName = complaint.getAssignedEmployee() != null
                ? complaint.getAssignedEmployee() + " "+ complaint.getAssignedEmployee()
                : null;
        this.createdAt = complaint.getCreatedAt();
    }


}
