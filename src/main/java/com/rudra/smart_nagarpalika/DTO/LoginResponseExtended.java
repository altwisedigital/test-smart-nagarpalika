package com.rudra.smart_nagarpalika.DTO;

public record LoginResponseExtended(
        String username,
        String role,
        EmployeeDetailsDTO employeeDetails
) {
}
