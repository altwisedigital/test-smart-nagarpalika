package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.*;
import com.rudra.smart_nagarpalika.Model.*;

import com.rudra.smart_nagarpalika.Repository.AlertRepo;
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

    private final WardsService wardsService;
    private final UserServices userServices;
    private final AlertsService alertsService;
    private final CategoryService categoryService;
    private final LocationService locationService;


    /// =======================     Citizen Section     ==============================

    @GetMapping("/by-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(
    ) {
        try {
            List<UserResponseDTO> users = userServices.getUsersByRole(UserRole.USER);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /// =======================     employee Section     ==============================

    /// create employees as a list at once (only for **POSTMAN**)


    // create in bul
    @PostMapping("/insert_emp")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeModel>> createEmployees(@RequestBody List<EmployeeRequestDTO> employeeDTOs) {
        List<EmployeeModel> savedEmployees = employeeService.createEmployeesInBulk(employeeDTOs);
        return ResponseEntity.ok(savedEmployees);
    }


    // to create employees
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeDTO) {
        log.info("Attempting to create employee with phone: {}", employeeDTO.getPhoneNumber());

        try {
            employeeService.createEmployee(employeeDTO);
            log.info("Employee created successfully for mobile: {}", employeeDTO.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Employee created successfully", true));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input while creating employee: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error while creating employee", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong. Please try again later."+e, false));
        }
    }

    @GetMapping("assignedComplaint/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> fetchAssignedComplaint(@PathVariable String name) {
        try {
            List<ComplaintResponseDTO> assignedComplaint = employeeService.fetchAssignedComplaint(name);

            if (assignedComplaint.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Collections.singletonMap("message", "No data found for the user: " + name));
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Here is the list of assigned complaints for user: " + name);
            response.put("count", assignedComplaint.size());
            response.put("data", assignedComplaint);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> errorResponse = Map.of(
                    "message", "Error fetching complaints",
                    "error", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEmployees() {
        try {
            List<EmployeeResponseDTO> employees = employeeService.getAllEmployees();

            if (employees.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("No employees found", true));
            }

            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Failed to fetch employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch employees", false));
        }
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            Optional<EmployeeResponseDTO> employee = employeeService.getEmployeeById(id);

            if (employee.isPresent()) {
                return ResponseEntity.ok(employee.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to fetch employee with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to fetch employee", false));
        }
    }

    @PutMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,
                                            @Valid @RequestBody EmployeeRequestDTO employeeDTO) {
        try {
            Optional<EmployeeResponseDTO> updatedEmployee = employeeService.updateEmployee(id, employeeDTO);

            if (updatedEmployee.isPresent()) {
                return ResponseEntity.ok(updatedEmployee.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input while updating employee: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error while updating employee with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong while updating employee.", false));
        }
    }

    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Long id) {
        try {
            boolean deleted = employeeService.deleteEmployee(id);

            if (deleted) {
                return ResponseEntity.ok(new ApiResponse("Employee deleted successfully", true));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to delete employee with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to delete employee", false));
        }
    }


//    // to get employees


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
    /* ====  create  list of   ==== */
    @PostMapping("/InsertDepartments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> InsertMultipleDepartments(@RequestBody List<DepartmentModel> department){
        try {
            deparmentService.addDepartmentList(department);
            return ResponseEntity.ok("Inserted this Departments: "+department);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }
    //create department
    @PostMapping("/create_department")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> CreateDepartments(@RequestBody  DepartmentDTO department){
        try {
            deparmentService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED).body("Department created successfully");
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cant create new Department"+e);
        }

    }

    // get all Department
    @GetMapping("/get_departments_admin")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> getAllDepartmentsForAdmin(){
        try {
            List<DepartmentModel> departments = deparmentService.GetAllDepartment();

            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Couldn't fetch the departments now"+e);
        }
    }


    // create department

    @PostMapping("/create_wards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> CreateWards(@RequestBody WardsDTO dto){
        try {
            wardsService.createWards(dto);
            return ResponseEntity.ok("Wards have been created");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("couldn't create the wards ERROR:"+e);
        }
    }

    //get all wards

    @GetMapping("/get_wards")
    @PreAuthorize("hasRole('ADMIN') ")

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



    // add wards as a list (Only for postman)..../.
    @PostMapping("/wards")
    public ResponseEntity<String> addAllWards(@RequestBody List<WardsModel> wardsList) {
        wardsService.addAllWards(wardsList);
        return ResponseEntity.ok("Wards added successfully");
    }



    // to store all the employee at once
//     @PostMapping("/create_all_employee")
//     public  ResponseEntity<?> createAllAtonce( @RequestBody List<EmployeeModel> employeeModel){
//
//         try {
//             employeeService.addAllEmployee(employeeModel);
//             return ResponseEntity.ok("employees saved Successfully ");
//         } catch (Exception e) {
//             return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//         }
//
//
//     }


    /// ==================     AlertSection       =================


    // create alert
    @PostMapping(value = "/create_alert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createAlert(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String type,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        AlertRequestDto dto= new AlertRequestDto();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setType(type);
        String response = alertsService.saveAlert(dto, image);
        return ResponseEntity.ok(response);
    }


    // Get all alerts
    @GetMapping("/get_all_alerts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAlerts() {
        try {
            List<AlertResponseDTO> alerts = alertsService.getAlerts();
            if (alerts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                return ResponseEntity.ok(alerts);
            }
        } catch (Exception e) {
            System.err.println("Error fetching alerts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to load alerts. Please try again later.");
        }
    }

//    // Get alert by ID
//    @GetMapping("delete_alert/{id}")
//    public ResponseEntity<?> getAlertById(@PathVariable Long id) {
//        try {
//            AlertsModel alert = alertsService.getAlertById(id);
//            if (alert != null) {
//                return ResponseEntity.ok(alert);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body("Alert not found with ID: " + id);
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Invalid request: " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println("Error fetching alert: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error occurred while fetching alert. Please try again later.");
//        }
//    }

    // Delete alert
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAlert(@PathVariable Long id) {
        try {
            AlertsModel alert = alertsService.getAlertById(id);
            if (alert != null) {
                boolean isDeleted = alertsService.DeleteAlert(id);
                if (isDeleted) {
                    return ResponseEntity.ok("Alert with ID " + id +"\n"+ alert+ " has been deleted successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to delete alert with ID: " + id);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Alert not found with ID: " + id);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error deleting alert: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while deleting alert. Please try again later.");
        }
    }


        // ---------------------- CATEGORY APIs ----------------------

    @PostMapping(value = "/create_category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(
            @RequestParam String  name,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
       CategoryRequest request = new CategoryRequest();
          request.setName(name);
        String response = categoryService.createCategory(request, image);

        return ResponseEntity.status(HttpStatus.CREATED).body("created"+response);
    }
  /*  @PostMapping(value = "/create_alert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createAlert(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String type,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        AlertRequestDto dto= new AlertRequestDto();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setType(type);
        String response = alertsService.saveAlert(dto, image);
        return ResponseEntity.ok(response);
    }*/

    // Get All Categories
        @GetMapping("/get/categories")
        public ResponseEntity<List<CategoryResponse>> getAllCategories() {
            return ResponseEntity.ok(categoryService.getAllCategories());
        }

        // Update Category
        @PutMapping("/categories/{id}")
        public ResponseEntity<CategoryResponse> updateCategory(
                @PathVariable Long id,
                @RequestPart("data") CategoryRequest request,
                @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
            return ResponseEntity.ok(categoryService.updateCategory(id, request, image));
        }

        // Delete Category
        @DeleteMapping("/categories/{id}")
        public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully");
        }

    // ---------------------- LOCATION APIs ----------------------

    // Create Location
        @PostMapping("/create_locations")
    public ResponseEntity<LocationResponse> createLocation(@RequestBody LocationRequest request) {
        return ResponseEntity.ok(locationService.createLocation(request));
    }

    // Get All Locations
    @GetMapping("/locations")
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    // Update Location
    @PutMapping("/locations/{id}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Long id,
            @RequestBody LocationRequest request) {
        return ResponseEntity.ok(locationService.updateLocation(id, request));
    }

    // Delete Location
    @DeleteMapping("/locations/{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok("Location deleted successfully");
    }


}