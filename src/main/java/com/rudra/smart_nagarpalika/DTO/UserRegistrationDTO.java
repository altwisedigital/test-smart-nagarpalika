package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.UserRole;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;
    private UserRole role; // Optional - will default to CITIZEN
}