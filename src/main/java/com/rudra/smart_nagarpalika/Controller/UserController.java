package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.UserRegistrationDTO;
import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import com.rudra.smart_nagarpalika.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userService;

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            // Convert DTO to Entity
            UserModel user = new UserModel();
            user.setUsername(registrationDTO.getUsername());
            user.setPassword(registrationDTO.getPassword());
            user.setFullName(registrationDTO.getFullName());
            user.setPhoneNumber(registrationDTO.getPhoneNumber());

            // Use provided role or default to CITIZEN
            UserRole role = registrationDTO.getRole() != null ? registrationDTO.getRole() : UserRole.USER;

            String message = userService.saveUserWithRole(user, role);
            return ResponseEntity.ok(new RegistrationResponse(message, true));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new RegistrationResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegistrationResponse("Registration failed", false));
        }
    }

    public record RegistrationResponse(String message, boolean success) {}
}