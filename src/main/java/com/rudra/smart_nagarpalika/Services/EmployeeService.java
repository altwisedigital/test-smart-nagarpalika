package com.rudra.smart_nagarpalika.Services;

//import com.rudra.smart_nagarpalika.DTO.employee;
import com.rudra.smart_nagarpalika.DTO.EmployeeDTO;
import com.rudra.smart_nagarpalika.Model.EmployeeModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import com.rudra.smart_nagarpalika.Repository.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    final private EmployeeRepo employeeRepo;



    public void createEmployee(EmployeeDTO employeeDTO) {
        boolean exists = employeeRepo.existsByMobile(employeeDTO.getMobile());
        if (exists) {
            throw new IllegalArgumentException("Employee with this mobile number already exists");
        }

        EmployeeModel employee = new EmployeeModel();
        employee.setFirstname(employeeDTO.getFirstname());
        employee.setLastname(employeeDTO.getLastname());
        employee.setMobile(employeeDTO.getMobile());
        employee.setRole(UserRole.EMPLOYEE);
        employee.setDepartment(employeeDTO.getDepartment());

        employeeRepo.save(employee);
    }

    public List<EmployeeModel> getAllEmployees() {
        return employeeRepo.findAll();
    }

    public EmployeeModel updateEmployee(EmployeeDTO employeeDTO) {
        // Fetch existing employee by mobile
        EmployeeModel existingEmployee = employeeRepo.findByMobile(employeeDTO.getMobile())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with mobile: " + employeeDTO.getMobile()));

        // Update fields on the existing object
        existingEmployee.setFirstname(employeeDTO.getFirstname());
        existingEmployee.setLastname(employeeDTO.getLastname());
        existingEmployee.setDepartment(employeeDTO.getDepartment());
        existingEmployee.setRole(UserRole.EMPLOYEE); // optional if always fixed

        // Save and return updated employee
        return employeeRepo.save(existingEmployee);
    }

    public void deleteEmployeeByMobile(String mobile) {
        boolean exists = employeeRepo.existsByMobile(mobile);
        if (exists) {
            employeeRepo.deleteByMobile(mobile);
        } else {
            throw new IllegalArgumentException("No employee exists with mobile: " + mobile);
        }
    }

}
