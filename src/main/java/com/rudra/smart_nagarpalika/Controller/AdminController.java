package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.*;
import com.rudra.smart_nagarpalika.Handler.AlertWebSocketHandler;
import com.rudra.smart_nagarpalika.Model.*;
import com.rudra.smart_nagarpalika.Repository.UserRepo;
import com.rudra.smart_nagarpalika.Services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final EmployeeService employeeService;
    private final ComplaintService complaintService;
    private final DeparmentService deparmentService;
    private final AlertWebSocketHandler webSocketHandler;
    private final WardsService wardsService;
    private final UserServices userServices;
    private final AlertsService alertsService;
    private final CategoryService categoryService;
    private final LocationService locationService;

    // ======================= Citizen Section ==============================

    @GetMapping("/by-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByRole() {
        log.info("Fetching users with role USER");
        try {
            List<UserResponseDTO> users = userServices.getUsersByRole(UserRole.USER);
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No users found with role USER", true));
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to fetch users by role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch users: " + e.getMessage(), false));
        }
    }

    @PutMapping("/update_users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserModel user) {
        log.info("Updating user with ID: {}", id);
        try {
            if (user == null || user.getUsername() == null) {
                log.warn("Invalid user data for ID: {}", id);
                return ResponseEntity.badRequest().body(new ApiResponse("User data is required", false));
            }
            UserResponseDTO updated = userServices.updateUser(id, user);
            log.info("User updated successfully: ID {}", id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for updating user ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error updating user ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to update user: " + e.getMessage(), false));
        }
    }

    // ======================= Employee Section ==============================

    @PostMapping("/insert_emp")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEmployees(@Valid @RequestBody List<EmployeeRequestDTO> employeeDTOs) {
        log.info("Creating {} employees in bulk", employeeDTOs.size());
        try {
            if (employeeDTOs == null || employeeDTOs.isEmpty()) {
                log.warn("No employee data provided for bulk insert");
                return ResponseEntity.badRequest().body(new ApiResponse("Employee data is required", false));
            }
            List<EmployeeModel> savedEmployees = employeeService.createEmployeesInBulk(employeeDTOs);
            log.info("Successfully created {} employees", savedEmployees.size());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployees);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for bulk employee creation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error in bulk employee creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to create employees: " + e.getMessage(), false));
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeDTO) {
        log.info("Creating employee with phone: {}", employeeDTO.getPhoneNumber());
        try {
            employeeService.createEmployee(employeeDTO);
            log.info("Employee created successfully: {}", employeeDTO.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Employee created successfully", true));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for employee creation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error creating employee: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to create employee: " + e.getMessage(), false));
        }
    }

    @GetMapping("/assignedComplaint/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> fetchAssignedComplaint(@PathVariable String name) {
        log.info("Fetching assigned complaints for employee: {}", name);
        try {
            List<ComplaintResponseDTO> assignedComplaints = employeeService.fetchAssignedComplaint(name);
            if (assignedComplaints.isEmpty()) {
                log.info("No complaints found for employee: {}", name);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No complaints found for employee: " + name, true));
            }
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Assigned complaints for employee: " + name);
            response.put("count", assignedComplaints.size());
            response.put("data", assignedComplaints);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error fetching complaints for {}: {}", name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch complaints: " + e.getMessage(), false));
        }
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEmployees() {
        log.info("Fetching all employees");
        try {
            List<EmployeeResponseDTO> employees = employeeService.getAllEmployees();
            if (employees.isEmpty()) {
                log.info("No employees found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No employees found", true));
            }
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Failed to fetch employees: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch employees: " + e.getMessage(), false));
        }
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        log.info("Fetching employee with ID: {}", id);
        try {
            Optional<EmployeeResponseDTO> employee = employeeService.getEmployeeById(id);
            return employee.map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        log.warn("Employee not found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Failed to fetch employee ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch employee: " + e.getMessage(), false));
        }
    }

    @PutMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequestDTO employeeDTO) {
        log.info("Updating employee with ID: {}", id);
        try {
            Optional<EmployeeResponseDTO> updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
            return updatedEmployee.map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        log.warn("Employee not found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for updating employee ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error updating employee ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to update employee: " + e.getMessage(), false));
        }
    }

    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        log.info("Deleting employee with ID: {}", id);
        try {
            boolean deleted = employeeService.deleteEmployee(id);
            if (deleted) {
                log.info("Employee deleted successfully: ID {}", id);
                return ResponseEntity.ok(new ApiResponse("Employee deleted successfully", true));
            }
            log.warn("Employee not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to delete employee ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to delete employee: " + e.getMessage(), false));
        }
    }

    // ======================= Complaint Section ==============================

    @GetMapping("/all_complaints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllComplaints() {
        log.info("Fetching all complaints");
        try {
            List<ComplaintResponseDTO> allComplaints = complaintService.getAllComplaints();
            if (allComplaints == null || allComplaints.isEmpty()) {
                log.info("No complaints found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No complaints found", true));
            }
            return ResponseEntity.ok(allComplaints);
        } catch (Exception e) {
            log.error("Failed to fetch complaints: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch complaints: " + e.getMessage(), false));
        }
    }

    // ======================= Department Section ==============================

    @PostMapping("/InsertDepartments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> insertMultipleDepartments(@Valid @RequestBody List<DepartmentModel> department) {
        log.info("Inserting {} departments", department.size());
        try {
            if (department == null || department.isEmpty()) {
                log.warn("No department data provided");
                return ResponseEntity.badRequest().body(new ApiResponse("Department data is required", false));
            }
            deparmentService.addDepartmentList(department);
            log.info("Successfully inserted {} departments", department.size());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Departments inserted successfully", true));
        } catch (Exception e) {
            log.error("Failed to insert departments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to insert departments: " + e.getMessage(), false));
        }
    }

    @PostMapping("/create_department")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDepartment(@Valid @RequestBody DepartmentDTO department) {
        log.info("Creating department: {}", department.getName());
        try {
            deparmentService.createDepartment(department);
            log.info("Department created successfully: {}", department.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Department created successfully", true));
        } catch (Exception e) {
            log.error("Failed to create department: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Failed to create department: " + e.getMessage(), false));
        }
    }

    @GetMapping("/get_departments_admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllDepartmentsForAdmin() {
        log.info("Fetching all departments for admin");
        try {
            List<DepartmentModel> departments = deparmentService.GetAllDepartment();
            if (departments.isEmpty()) {
                log.info("No departments found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No departments found", true));
            }
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            log.error("Failed to fetch departments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch departments: " + e.getMessage(), false));
        }
    }

    // ======================= Wards Section ==============================

    @PostMapping("/create_wards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createWards(@Valid @RequestBody WardsDTO dto) {
        log.info("Creating ward: {}", dto.getName());
        try {
            wardsService.createWards(dto);
            log.info("Ward created successfully: {}", dto.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Ward created successfully", true));
        } catch (Exception e) {
            log.error("Failed to create ward: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Failed to create ward: " + e.getMessage(), false));
        }
    }

    @GetMapping("/get_wards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllWards() {
        log.info("Fetching all wards");
        try {
            List<WardsModel> allWards = wardsService.GetAllWards();
            if (allWards.isEmpty()) {
                log.info("No wards found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No wards found", true));
            }
            return ResponseEntity.ok(allWards);
        } catch (Exception e) {
            log.error("Failed to fetch wards: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch wards: " + e.getMessage(), false));
        }
    }

    @PostMapping("/wards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addAllWards(@Valid @RequestBody List<WardsModel> wardsList) {
        log.info("Adding {} wards in bulk", wardsList.size());
        try {
            if (wardsList == null || wardsList.isEmpty()) {
                log.warn("No ward data provided");
                return ResponseEntity.badRequest().body(new ApiResponse("Ward data is required", false));
            }
            wardsService.addAllWards(wardsList);
            log.info("Successfully added {} wards", wardsList.size());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Wards added successfully", true));
        } catch (Exception e) {
            log.error("Failed to add wards: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to add wards: " + e.getMessage(), false));
        }
    }

    // ======================= Alert Section ==============================

    @PostMapping(value = "/create_alert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAlert(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String type,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info("Creating alert: title={}, type={}", title, type);
        try {
            if (title == null || title.trim().isEmpty() || description == null || type == null) {
                log.warn("Invalid alert data: title={}, description={}, type={}", title, description, type);
                return ResponseEntity.badRequest().body(new ApiResponse("Title, description, and type are required", false));
            }
            if (image != null && !image.isEmpty()) {
                log.info("Alert image provided: {}, size: {}, content-type: {}",
                        image.getOriginalFilename(), image.getSize(), image.getContentType());
            }
            AlertRequestDto dto = new AlertRequestDto();
            dto.setTitle(title.trim());
            dto.setDescription(description.trim());
            dto.setType(type.trim());
            AlertsModel savedAlert = alertsService.saveAlert(dto, image);
            webSocketHandler.broadcastNewAlert(savedAlert);
            log.info("Alert created and broadcasted: {} (ID: {}, Active Connections: {})",
                    savedAlert.getTitle(), savedAlert.getId(), webSocketHandler.getActiveSessionCount());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Alert created and broadcasted successfully",
                    "alert", savedAlert,
                    "activeConnections", webSocketHandler.getActiveSessionCount()
            ));
        } catch (Exception e) {
            log.error("Failed to create alert: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to create alert: " + e.getMessage(), false));
        }
    }

    @GetMapping("/get_all_alerts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAlerts() {
        log.info("Fetching all alerts");
        try {
            List<AlertResponseDTO> alerts = alertsService.getAlerts();
            if (alerts.isEmpty()) {
                log.info("No alerts found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No alerts found", true));
            }
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            log.error("Failed to fetch alerts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch alerts: " + e.getMessage(), false));
        }
    }

    @GetMapping("/ws/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getWebSocketStatus() {
        log.info("Fetching WebSocket status");
        try {
            return ResponseEntity.ok(Map.of(
                    "activeConnections", webSocketHandler.getActiveSessionCount(),
                    "status", "WebSocket server running"
            ));
        } catch (Exception e) {
            log.error("Failed to fetch WebSocket status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch WebSocket status: " + e.getMessage(), false));
        }
    }

    // ======================= Category Section ==============================

    @PostMapping(value = "/create_category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(
            @RequestParam String name,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info("Creating category: {}", name);
        try {
            if (name == null || name.trim().isEmpty()) {
                log.warn("Invalid category name: {}", name);
                return ResponseEntity.badRequest().body(new ApiResponse("Category name is required", false));
            }
            if (image != null && !image.isEmpty()) {
                log.info("Category image provided: {}, size: {}, content-type: {}",
                        image.getOriginalFilename(), image.getSize(), image.getContentType());
            }
            CategoryRequest request = new CategoryRequest();
            request.setName(name.trim());
            String response = categoryService.createCategory(request, image);
            log.info("Category created: {}", response);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Category created: " + response, true));
        } catch (Exception e) {
            log.error("Failed to create category: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to create category: " + e.getMessage(), false));
        }
    }

    @GetMapping("/get/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCategories() {
        log.info("Fetching all categories");
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            if (categories.isEmpty()) {
                log.info("No categories found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No categories found", true));
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Failed to fetch categories: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch categories: " + e.getMessage(), false));
        }
    }

    @PutMapping(value = "/categories/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestPart("data") @Valid CategoryRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info("Updating category ID: {}", id);
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                log.warn("Invalid category name for ID: {}", id);
                return ResponseEntity.badRequest().body(new ApiResponse("Category name is required", false));
            }
            if (image != null && !image.isEmpty()) {
                log.info("Category image provided: {}, size: {}, content-type: {}",
                        image.getOriginalFilename(), image.getSize(), image.getContentType());
            }
            CategoryResponse updated = categoryService.updateCategory(id, request, image);
            log.info("Category updated successfully: ID {}", id);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            log.error("Failed to upload category image for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to upload category image: " + e.getMessage(), false));
        } catch (Exception e) {
            log.error("Failed to update category ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to update category: " + e.getMessage(), false));
        }
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        log.info("Deleting category ID: {}", id);
        try {
            categoryService.deleteCategory(id);
            log.info("Category deleted successfully: ID {}", id);
            return ResponseEntity.ok(new ApiResponse("Category deleted successfully", true));
        } catch (Exception e) {
            log.error("Failed to delete category ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to delete category: " + e.getMessage(), false));
        }
    }

    // ======================= Location Section ==============================

    @PostMapping("/create_locations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createLocation(@Valid @RequestBody LocationRequest request) {
        log.info("Creating location: {}", request.getName());
        try {
            LocationResponse location = locationService.createLocation(request);
            log.info("Location created successfully: {}", request.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(location);
        } catch (Exception e) {
            log.error("Failed to create location: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Failed to create location: " + e.getMessage(), false));
        }
    }

    @GetMapping("/locations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllLocations() {
        log.info("Fetching all locations");
        try {
            List<LocationResponse> locations = locationService.getAllLocations();
            if (locations.isEmpty()) {
                log.info("No locations found");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No locations found", true));
            }
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            log.error("Failed to fetch locations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch locations: " + e.getMessage(), false));
        }
    }

    @PutMapping("/locations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationRequest request) {
        log.info("Updating location ID: {}", id);
        try {
            LocationResponse updated = locationService.updateLocation(id, request);
            log.info("Location updated successfully: ID {}", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Failed to update location ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to update location: " + e.getMessage(), false));
        }
    }

    @DeleteMapping("/locations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        log.info("Deleting location ID: {}", id);
        try {
            locationService.deleteLocation(id);
            log.info("Location deleted successfully: ID {}", id);
            return ResponseEntity.ok(new ApiResponse("Location deleted successfully", true));
        } catch (Exception e) {
            log.error("Failed to delete location ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to delete location: " + e.getMessage(), false));
        }
    }

    // Record for consistent API responses
    public record ApiResponse(String message, boolean success) {}
}