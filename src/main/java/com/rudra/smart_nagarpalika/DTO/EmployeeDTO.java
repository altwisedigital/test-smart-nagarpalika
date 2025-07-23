package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.Departments;
import com.rudra.smart_nagarpalika.Model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmployeeDTO {

    private  long id;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    private UserRole role;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
    private String mobile;

    private Departments department;
}
