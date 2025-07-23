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


}
