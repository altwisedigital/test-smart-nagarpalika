package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.EmployeeDTO;
import com.rudra.smart_nagarpalika.Model.Departments;
import com.rudra.smart_nagarpalika.Model.EmployeeModel;
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

    @GetMapping("/get_employees")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EmployeeModel> getAllEmployees() {
        return employeeService.getAllEmployees();
    }


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
}
