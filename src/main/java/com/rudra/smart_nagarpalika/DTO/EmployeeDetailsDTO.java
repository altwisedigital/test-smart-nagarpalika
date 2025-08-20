package com.rudra.smart_nagarpalika.DTO;

import java.util.List;

public record EmployeeDetailsDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        String departmentName,
        List<String> wardNames
) {}