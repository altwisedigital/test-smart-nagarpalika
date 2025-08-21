package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import com.rudra.smart_nagarpalika.Model.WardsModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;


@Data
public class EmployeeRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,20}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotNull(message = "Department is required")
    private Long departmentId;

    private  String username;

    private String password;

    private String position;

    private List<Long> wardsId;

//    @NotBlank(message = "Role is required")

    private String role;
}
