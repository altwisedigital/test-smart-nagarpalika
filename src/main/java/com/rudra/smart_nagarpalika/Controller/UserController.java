package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.DTO.UserRegistrationDTO;
import com.rudra.smart_nagarpalika.Model.*;
import com.rudra.smart_nagarpalika.Services.DeparmentService;
import com.rudra.smart_nagarpalika.Services.IpServices;
import com.rudra.smart_nagarpalika.Services.UserServices;
import com.rudra.smart_nagarpalika.Services.WardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/citizen")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userService;
    private final DeparmentService deparmentService;
     private final WardsService wardsService;

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
                        complaint.getWard() != null ? complaint.getWard().getName()  : null,
                        complaint.getLocation(),
                        complaint.getImages() != null
                                ? complaint.getImages().stream()
                                .map(
                                        img -> "http://" + IpServices.getCurrentIP() + ":8080/uploads/citizen_image_uploads/" + img.getImageUrl()
                                        ///  calling the local io address as we fetch the complaints for testing purpose
                                )
                                .toList()
                                : List.of(),
                        complaint.getSubmittedBy(),
                        complaint.getStatus().name(),
                        complaint.getAssignedEmployee() != null
                                ? complaint.getAssignedEmployee()+ " " + complaint.getAssignedEmployee()
                                : null,
                        complaint.getCreatedAt()
                )).toList();

        complaints.forEach(System.out::println); // or log it

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

}