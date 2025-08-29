package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.EmployeeDetailsDTO;
import com.rudra.smart_nagarpalika.DTO.UserResponseDTO;
import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import com.rudra.smart_nagarpalika.Repository.EmployeeRepo;
import com.rudra.smart_nagarpalika.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServices {

    private final UserRepo userRepo;
    private final EmployeeRepo employeeRepo;

    /**
     * Get all users by specific role
     * @param role UserRole enum
     * @return List of UserResponseDTO
     */
    public List<UserResponseDTO> getUsersByRole(UserRole role) {
        List<UserModel> users = userRepo.findByRole(role);
        return users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    public String saveUser(UserModel user) {
        // Validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Check if user already exists
        Optional<UserModel> existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Set default values if not provided
        if (user.getRole() == null) {
            user.setRole(UserRole.USER); // Default to CITIZEN instead of ADMIN
        }

        user.setCreatedAt(LocalDateTime.now());

        // Save user
        UserModel savedUser = userRepo.save(user);
        return "User registered successfully with ID: " + savedUser.getId();
    }

    // Additional method to register user with specific role
    public String saveUserWithRole(UserModel user, UserRole role) {
        user.setRole(role);
        return saveUser(user);
    }

    // Method to get user by username
    public Optional<UserModel> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }


    public boolean validateCredentials(String username, String password) {
        Optional<UserModel> userOpt = userRepo.findByUsernameAndPassword(username, password);
        return userOpt.isPresent() && userOpt.get().getRole() == UserRole.ADMIN;
    }
}