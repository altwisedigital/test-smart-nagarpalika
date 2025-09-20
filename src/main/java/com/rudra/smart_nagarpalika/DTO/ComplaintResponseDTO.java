package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Model.ImageByEmployeeModel;
import com.rudra.smart_nagarpalika.Model.ImageByUserModel;
import com.rudra.smart_nagarpalika.Model.VideoByUserModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ComplaintResponseDTO {

    private Long id;
    private String description;
    private String departmentName; // maps to DepartmentModel
    private String wardName;
    private String location;
    private List<String> imageUrls; // Images uploaded by user
    private List<String> videoUrls; // Videos uploaded by user
    private String submittedBy;
    private double latitude;
    private double longitude;
    private String status;
    private String assignedEmployeeName;
    private LocalDateTime createdAt;

    // Employee workflow details
    private String employeeRemark;
    private List<String> employeeImages;
    private LocalDateTime completedAt;

    // Constructor to map from ComplaintModel
    public ComplaintResponseDTO(ComplaintModel complaint) {
        this.id = complaint.getId();
        this.description = complaint.getDescription();
        this.departmentName = complaint.getDepartment() != null ? complaint.getDepartment().getName() : null;
        this.wardName = complaint.getWard() != null ? complaint.getWard().getName() : null;
        this.location = complaint.getLocation();

        this.imageUrls = complaint.getImages() != null
                ? complaint.getImages().stream().map(ImageByUserModel::getImageUrl).toList()
                : List.of();

        this.videoUrls = complaint.getVideo() != null
                ? complaint.getVideo().stream().map(VideoByUserModel::getVideoUrl).toList()
                : List.of();

        this.submittedBy = complaint.getSubmittedBy();
        this.latitude = complaint.getLatitude();
        this.longitude = complaint.getLongitude();
        this.status = complaint.getStatus().name();

        this.assignedEmployeeName = complaint.getAssignedEmployee() != null
                ? complaint.getAssignedEmployee().getFirstName() + " " + complaint.getAssignedEmployee().getLastName()
                : null;

        this.createdAt = complaint.getCreatedAt();
        this.employeeRemark = complaint.getEmployeeRemarks();

        this.employeeImages = complaint.getEmployeeImages() != null
                ? complaint.getEmployeeImages().stream().map(ImageByEmployeeModel::getImageUrl).toList()
                : List.of();

        this.completedAt = complaint.getCompletedAt();
    }
}
