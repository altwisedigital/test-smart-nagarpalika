package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.DTO.UserRegistrationDTO;
import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import com.rudra.smart_nagarpalika.Services.DeparmentService;
import com.rudra.smart_nagarpalika.Services.UserServices;
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
                .map(ComplaintResponseDTO::new)
                .collect(Collectors.toList());
        complaints.forEach(System.out::println); // or log it

        return ResponseEntity.ok(complaintDTOs);
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