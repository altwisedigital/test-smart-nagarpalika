package com.rudra.smart_nagarpalika.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String username;
    private String fullName;
    private String role;
    private EmployeeResponseDTO employeeDetails; // null if not employee
}
