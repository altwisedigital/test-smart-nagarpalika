package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.DTO.EmployeeDTO;
import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Model.Departments;
import com.rudra.smart_nagarpalika.Model.EmployeeModel;
import com.rudra.smart_nagarpalika.Services.ComplaintService;
import com.rudra.smart_nagarpalika.Services.EmployeeService;
import com.rudra.smart_nagarpalika.Services.UserServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final EmployeeService employeeService;
    private final UserServices userServices;
    private final ComplaintService complaintService;


    // to create employees
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("Attempting to create employee: {}", employeeDTO);

        try {
            employeeService.createEmployee(employeeDTO);
            log.info("Employee created successfully for mobile: {}", employeeDTO.getMobile());
            return ResponseEntity.ok(new ApiResponse("Employee created successfully", true));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input while creating employee: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error while creating employee", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong. Please try again later.", false));
        }
    }

    // to get employees
    @GetMapping("/get_employees")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EmployeeModel> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // to update the employee
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeModel updatedEmployee = employeeService.updateEmployee(employeeDTO);
            return ResponseEntity.ok(updatedEmployee);  // return updated data
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong while updating employee.");
        }
    }


    // to delete the employee by mobile

    @DeleteMapping("/delete/{mobile}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable String mobile) {
        employeeService.deleteEmployeeByMobile(mobile);
        return ResponseEntity.ok("Employee Deleted");
    }


    // to get all the departments

    @GetMapping("/departments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllDepartments() {
        try {
            var departments = Arrays.stream(Departments.values())
                    .map(dep -> new DepartmentResponse(dep.name(), dep.getDisplayName()))
                    .toList();

            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            log.error("Failed to fetch departments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch departments", false));
        }
    }

    public record DepartmentResponse(String code, String displayName) {}


    public record ApiResponse(String message, boolean success) {}




    //  to get all the complaints

    @GetMapping("/all_complaints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllComplaints() {
        try {
            List<ComplaintResponseDTO> allComplaints = complaintService.getAllComplaints();

            if (allComplaints == null || allComplaints.isEmpty()) {
                return ResponseEntity.noContent().build(); // ✅ Proper empty 204 response
            }

            return ResponseEntity.ok(allComplaints); // ✅ Wrap list in ResponseEntity
        } catch (Exception e) {
            log.error("Failed to fetch complaints", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch complaints", false));
        }
    }



}
