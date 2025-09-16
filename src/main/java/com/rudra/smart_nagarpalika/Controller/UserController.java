package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.*;
import com.rudra.smart_nagarpalika.Model.*;
import com.rudra.smart_nagarpalika.Services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.rmi.server.LogStream.log;

@RestController
@RequestMapping("/citizen")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userService;
    private final DeparmentService deparmentService;
     private final WardsService wardsService;
     private final AlertsService alertsService;
    private  final  LocationService locationService;
    private final CategoryService categoryService;
    @GetMapping("/complaints/by-username")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getComplaintsByUsername(@RequestParam String username) {
        Optional<UserModel> user = userService.getUserByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with username: " + username);
        }

        List<ComplaintModel> complaints = user.get().getComplaints();

        List<ComplaintResponseDTO> complaintDTOs = complaints.stream()
                .map(complaint -> new ComplaintResponseDTO(
                        complaint.getId(),
                        complaint.getDescription(),
                        complaint.getDepartment() != null ? complaint.getDepartment().getName() : null,
                        complaint.getWard() != null ? complaint.getWard().getName() : null,
                        complaint.getLocation(),

                        // citizen images
                        complaint.getImages() != null
                                ? complaint.getImages().stream()
                                .map(img -> "http://" + IpServices.getCurrentIP()
                                        + ":8080/uploads/citizen_image_uploads/"
                                        + img.getImageUrl())
                                .toList()
                                : List.of(),

                        // citizen videos
                        complaint.getVideo() != null
                                ? complaint.getVideo().stream()
                                .map(video -> "http://" + IpServices.getCurrentIP()
                                        + ":8080/uploads/citizen_video_uploads/"
                                        + video.getVideoUrl())
                                .toList()
                                : List.of(),

                        complaint.getSubmittedBy(),
                        complaint.getLatitude(),
                        complaint.getLongitude(),
                        complaint.getStatus().name(),
                        complaint.getAssignedEmployee() != null
                                ? complaint.getAssignedEmployee().getFirstName() + " " + complaint.getAssignedEmployee().getLastName()
                                : null,
                        complaint.getCreatedAt(),
                        complaint.getEmployeeRemarks(),

                        // employee images
                        complaint.getEmployeeImages() != null
                                ? complaint.getEmployeeImages().stream()
                                .map(img -> "http://" + IpServices.getCurrentIP()
                                        + ":8080/uploads/employee_image_uploads/"
                                        + img.getImageUrl())
                                .toList()
                                : List.of(),

                        complaint.getCompletedAt()
                ))
                .toList();

        return ResponseEntity.ok(complaintDTOs);
    }


    @GetMapping("/get_wards")
    @PreAuthorize("hasRole('USER') ")

    public ResponseEntity<?> GetAllWards(){
        try {
            List<WardsModel> allWards = wardsService.GetAllWards();
            return ResponseEntity.ok(
                    allWards
            );

        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("couldn't fetch the wards ERROR : "+e);
        }
    }


    //get departments
    @GetMapping("/get_departments_user")
    @PreAuthorize( "hasRole('USER')")

    public ResponseEntity<?> getAllDepartmentsForAdmin(){
        try {
            List<DepartmentModel> departments = deparmentService.GetAllDepartment();

            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Couldn't fetch the departments now"+e);
        }
    }

    /// ============================ alert department API'S ======================================

    @GetMapping("/get_all_alerts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AlertResponseDTO>> getAllAlerts() {
        try {
            List<AlertResponseDTO> alerts = alertsService.getAlerts();

            if (alerts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.ok(alerts);
            }
        } catch (Exception e) {
            log("Error fetching alerts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /// ================================= locations apis's ==========================================

    @GetMapping("/locations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    /// =============================== categories api's ========================================

    @GetMapping("/categories")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

}