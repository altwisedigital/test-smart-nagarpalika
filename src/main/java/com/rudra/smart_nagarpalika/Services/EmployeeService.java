package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.EmployeeRequestDTO;
import com.rudra.smart_nagarpalika.DTO.EmployeeResponseDTO;
import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import com.rudra.smart_nagarpalika.Model.EmployeeModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import com.rudra.smart_nagarpalika.Model.WardsModel;
import com.rudra.smart_nagarpalika.Repository.EmployeeRepo;
import com.rudra.smart_nagarpalika.Repository.DepartmentRepo;
import com.rudra.smart_nagarpalika.Repository.WardsRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


    // Create Employee
    @Transactional
    public void createEmployee(EmployeeRequestDTO requestDTO) {
        // checking if employee is being repeated
        boolean exist = employeeRepository.existsByPhoneNumber(requestDTO.getPhoneNumber());

        if (exist){
             throw new IllegalArgumentException("Employee with Phone number :"+ requestDTO.getPhoneNumber()+"already exists");
        }
        //validate the department.....
        DepartmentModel department = departmentRepo.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + requestDTO.getDepartmentId()));

        // Validate and fetch wards
        List<WardsModel> wards = null;
        if (requestDTO.getWardsId() != null && !requestDTO.getWardsId().isEmpty()) {
            wards = wardsRepo.findAllById(requestDTO.getWardsId());
            if (wards.size() != requestDTO.getWardsId().size()) {
                throw new RuntimeException("One or more ward IDs are invalid");
            }
        }

        EmployeeModel employee = EmployeeModel.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .phoneNumber(requestDTO.getPhoneNumber())
                .role(UserRole.EMPLOYEE)
                .department(department)
                .wards(wards)
                .build();

        employeeRepository.save(employee);
        log.info("Employee created successfully with phone number: {}", requestDTO.getPhoneNumber());
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