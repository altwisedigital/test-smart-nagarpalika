package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.DTO.EmployeeDetailsDTO;
import com.rudra.smart_nagarpalika.DTO.EmployeeRequestDTO;
import com.rudra.smart_nagarpalika.DTO.EmployeeResponseDTO;
import com.rudra.smart_nagarpalika.Model.*;
import com.rudra.smart_nagarpalika.Repository.EmployeeRepo;
import com.rudra.smart_nagarpalika.Repository.DepartmentRepo;
import com.rudra.smart_nagarpalika.Repository.UserRepo;
import com.rudra.smart_nagarpalika.Repository.WardsRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    // fetch complaint by employee name
    public List<ComplaintResponseDTO> fetchAssignedComplaint(String name){
        EmployeeModel emp = employeeRepository.findByfirstName(name);
        if (emp == null) {
            throw new RuntimeException("Employee not found with name: " +  name);
        }
        return emp.getAssignedComplaints()
                .stream()
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
                                ? complaint.getAssignedEmployee().getFirstName()   + " " + complaint.getAssignedEmployee().getLastName()
                                : null,
                        complaint.getCreatedAt()))
                .collect(Collectors.toList());

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