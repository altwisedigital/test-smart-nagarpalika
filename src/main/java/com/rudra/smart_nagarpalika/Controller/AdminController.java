package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.*;
import com.rudra.smart_nagarpalika.Model.*;
import com.rudra.smart_nagarpalika.Repository.DepartmentRepo;
import com.rudra.smart_nagarpalika.Repository.EmployeeRepo;
import com.rudra.smart_nagarpalika.Repository.WardsRepo;
import com.rudra.smart_nagarpalika.Services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final EmployeeService employeeService;
    private final UserServices userServices;
    private final ComplaintService complaintService;
    private final DeparmentService deparmentService;

    private final WardsService wardsService;
    private final EmployeeRepo employeeRepo;
    private final WardsRepo wardsRepo;
    private final DepartmentRepo departmentRepo;


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
                    .body(new ApiResponse("Something went wrong. Please try again later.", false));
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
//    @GetMapping("/get_employees")
//    @PreAuthorize("hasRole('ADMIN')")
//    public List<EmployeeModel> getAllEmployees() {
//        return employeeService.getAllEmployees();
//    }
//
//    // to update the employee
//    @PutMapping("/update")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDTO employeeDTO) {
//        try {
//            EmployeeModel updatedEmployee = employeeService.updateEmployee(employeeDTO);
//            return ResponseEntity.ok(updatedEmployee);  // return updated data
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Something went wrong while updating employee.");
//        }
//    }


    // to delete the employee by mobile

//    @DeleteMapping("/delete/{mobile}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> deleteEmployee(@PathVariable String mobile) {
//        employeeService.deleteEmployeeByMobile(mobile);
//        return ResponseEntity.ok("Employee Deleted");
//    }


    // to get all the departments

//    @GetMapping("/departments")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> getAllDepartments() {
//        try {
//            var departments = Arrays.stream(Departments.values())
//                    .map(dep -> new DepartmentResponse(dep.name(), dep.getDisplayName()))
//                    .toList();
//
//            return ResponseEntity.ok(departments);
//        } catch (Exception e) {
//            log.error("Failed to fetch departments", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse("Failed to fetch departments", false));
//        }
//    }

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
                    "wards:"+allWards
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

}
