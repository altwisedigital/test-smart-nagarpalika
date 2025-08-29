package com.rudra.smart_nagarpalika.DTO;

import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String username;
    private String phoneNumber; // Fixed typo from phonNumber
    private UserRole role;
    private int complaints;

    public UserResponseDTO(UserModel user) {
        this.id = user.getId();
        this.name = user.getFullName();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber(); // Fixed assignment
        this.complaints = user.getComplaints().size();
        this.role = user.getRole();
    }
}