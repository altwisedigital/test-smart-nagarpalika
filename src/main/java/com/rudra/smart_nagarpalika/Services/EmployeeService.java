package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.DTO.EmployeeDetailsDTO;
import com.rudra.smart_nagarpalika.DTO.EmployeeRequestDTO;
import com.rudra.smart_nagarpalika.DTO.EmployeeResponseDTO;
import com.rudra.smart_nagarpalika.Model.*;
import com.rudra.smart_nagarpalika.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {


    private final EmployeeRepo employeeRepository;
    private final DepartmentRepo departmentRepo;
    private final WardsRepo wardsRepo;
    private final EmployeeRepo employeeRepo;
    private final UserRepo userRepo;
    private final ComplaintRepo complaintRepo;
    private final ImageByEmployeeRepo  imageByEmployeeRepo;
    private final ImageService imageService;




    //get employee details
    public EmployeeDetailsDTO getEmployeeDetailsByUser(UserModel user) {
        return employeeRepo.findByUserAccount(user)
                .map(emp -> new EmployeeDetailsDTO(
                        emp.getFirstName(),
                        emp.getLastName(),
                        emp.getPhoneNumber(),
                        emp.getDepartment().getName(),
                        emp.getWards().stream()
                                .map(ward -> ward.getId() + " - " + ward.getName())
                                .toList()
                ))
                .orElse(null);
    }

    public List<ComplaintResponseDTO> fetchAssignedComplaint(String name) {
        EmployeeModel emp = employeeRepository.findByfirstName(name);
        if (emp == null) {
            throw new RuntimeException("Employee not found with name: " + name);
        }

        return emp.getAssignedComplaints()
                .stream()
                .map(complaint -> new ComplaintResponseDTO(
                        complaint.getId(),
                        complaint.getDescription(),
                        complaint.getDepartment() != null ? complaint.getDepartment().getName() : null,
                        complaint.getWard() != null ? complaint.getWard().getName() : null,
                        complaint.getLocation(),

                        // user images
                        complaint.getImages() != null
                                ? complaint.getImages().stream()
                                .map(img -> "http://" + IpServices.getCurrentIP()
                                        + ":8080/uploads/citizen_image_uploads/"
                                        + img.getImageUrl())
                                .toList()
                                : List.of(),

                        // user videos
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
    }


    // create multiple eployees at once

    @Transactional
    public List<EmployeeModel> createEmployeesInBulk(List<EmployeeRequestDTO> employeeDTOs) {
        List<EmployeeModel> employees = employeeDTOs.stream().map(dto -> {
            DepartmentModel department = departmentRepo.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID: " + dto.getDepartmentId()));

            List<WardsModel> wards = (dto.getWardsId() != null && !dto.getWardsId().isEmpty())
                    ? wardsRepo.findAllById(dto.getWardsId())
                    : List.of();

            return EmployeeModel.builder()
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .phoneNumber(dto.getPhoneNumber())
                    .role(UserRole.EMPLOYEE)
                    .department(department)
                    .wards(wards)

                    .createdAt(LocalDateTime.now())
                    .build();
        }).collect(Collectors.toList());

        return employeeRepository.saveAll(employees);
    }

    // Create Employee
    @Transactional
    public void createEmployee(EmployeeRequestDTO requestDTO) {
        // 1. Check if employee already exists
        if (employeeRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("Employee with Phone number: " + requestDTO.getPhoneNumber() + " already exists");
        }

        // 2. Validate department
        DepartmentModel department = departmentRepo.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + requestDTO.getDepartmentId()));

        // 3. Validate and fetch wards
        List<WardsModel> wards = null;
        if (requestDTO.getWardsId() != null && !requestDTO.getWardsId().isEmpty()) {
            wards = wardsRepo.findAllById(requestDTO.getWardsId());
            if (wards.size() != requestDTO.getWardsId().size()) {
                throw new RuntimeException("One or more ward IDs are invalid");
            }
        }

        // 4. Create the User account for this employee
        String generatedUsername = requestDTO.getUsername().trim();
        String generatedPassword =  requestDTO.getPassword().trim();
        // checking if username exists ./.
        if (userRepo.existsByUsername(requestDTO.getUsername().trim())) {
            throw new IllegalArgumentException("Username already taken: " + requestDTO.getUsername().trim());
        }
        UserModel user = new UserModel();
        user.setUsername(generatedUsername);
        user.setPassword(generatedPassword);
        user.setFullName(requestDTO.getFirstName()+ requestDTO.getLastName());
        user.setPhoneNumber(requestDTO.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.EMPLOYEE);
        userRepo.save(user);

        // 5. Create the Employee and link User
        EmployeeModel employee = EmployeeModel.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .phoneNumber(requestDTO.getPhoneNumber())
                .role(UserRole.EMPLOYEE)
                .department(department)
                .wards(wards)
                .userAccount(user) // 🔹 Assign user here
                .build();

        employeeRepository.save(employee);

        log.info("Employee created successfully with phone number: {}", requestDTO.getPhoneNumber());
        log.info("Generated username: {}, password: {}", generatedUsername, generatedPassword);
    }

    // Get all Employees
    public List<EmployeeResponseDTO> getAllEmployees() {
        List<EmployeeModel> employees = employeeRepository.findAll();
        return employees.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get Employee by ID
    public Optional<EmployeeResponseDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(this::mapToResponseDTO);
    }

    // Update Employee
    public Optional<EmployeeResponseDTO> updateEmployee(Long id, EmployeeRequestDTO requestDTO) {
        Optional<EmployeeModel> optionalEmployee = employeeRepository.findById(id);
        DepartmentModel department = departmentRepo.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + requestDTO.getDepartmentId()));

        if (optionalEmployee.isPresent()) {
            EmployeeModel employee = optionalEmployee.get();
            employee.setFirstName(requestDTO.getFirstName());
            employee.setLastName(requestDTO.getLastName());
//            employee.setEmail(requestDTO.getEmail());
            employee.setPhoneNumber(requestDTO.getPhoneNumber());
//            employee.setPosition(requestDTO.getPosition());
            employee.setDepartment(department);
//            employee.setJoiningDate(requestDTO.getJoiningDate());

            EmployeeModel updatedEmployee = employeeRepository.save(employee);
            return Optional.of(mapToResponseDTO(updatedEmployee));
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public String completeComplaint(Long complaintId, String repairDescription, List<MultipartFile> imageFiles) {
        // 1. Fetch complaint
        ComplaintModel complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        List<String> uploadedPaths = new ArrayList<>();

        // 2. Save image files
        for (MultipartFile file : imageFiles) {
            if (!file.isEmpty()) {
                try {
                    String path = imageService.saveEmployeeImage(file);
                             // ✅ same as user
                    uploadedPaths.add(path);
                } catch (IOException e) {
                    log.error("Failed to save image: {}", file.getOriginalFilename(), e);
                    throw new RuntimeException("Image upload failed: " + file.getOriginalFilename());
                }
            }
        }

        // 3. Optional validation
        if (uploadedPaths.isEmpty()) {
            throw new RuntimeException("No valid images were uploaded by employee");
        }

        // 4. Update complaint details
        complaint.setEmployeeRemarks(repairDescription);
        complaint.setStatus(ComplaintStatus.Resolved); // ✅ enum update
        complaint.setCompletedAt(LocalDateTime.now());

        // 5. Save images in employee image table
        List<ImageByEmployeeModel> imagesByEmployee = uploadedPaths.stream().map(url -> {
            ImageByEmployeeModel img = new ImageByEmployeeModel();
            img.setImageUrl(url);
            img.setComplaint(complaint); // set FK
            return img;
        }).toList();

        complaint.getEmployeeImages().addAll(imagesByEmployee);

        // 6. Save complaint (cascade saves employee images too)
        complaintRepo.save(complaint);

        return "Complaint updated successfully by employee";
    }



    // Delete Employee
    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private EmployeeResponseDTO mapToResponseDTO(EmployeeModel model) {
        return new EmployeeResponseDTO(model);
    }

}