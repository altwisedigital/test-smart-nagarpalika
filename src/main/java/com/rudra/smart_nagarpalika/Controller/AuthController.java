package com.rudra.smart_nagarpalika.Controller;


import com.rudra.smart_nagarpalika.DTO.LoginRequestDTO;
import com.rudra.smart_nagarpalika.DTO.UserRegistrationDTO;
import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import com.rudra.smart_nagarpalika.Services.UserServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserServices userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate using username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            // Get user by username
            Optional<UserModel> user = userService.getUserByUsername(request.username());
            if (user.isEmpty()) {
                return ResponseEntity.status(404).body(new ApiResponse("User not found", false));
            }

            // Extract role (e.g., ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_CITIZEN)
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_UNKNOWN");

            log.info("User {} logged in with role {}", user.get().getUsername(), role);

            return ResponseEntity.ok(new LoginResponse(user.get().getUsername(), role));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse("Invalid credentials", false));
        } catch (Exception e) {
            log.error("Login failed", e);
            return ResponseEntity.internalServerError().body(new ApiResponse("Login failed", false));
        }
    }


    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String username, String role) {}
    public record ApiResponse(String message, boolean success) {}


    @PostMapping("/create_user")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            // Convert DTO to Entity
            UserModel user = new UserModel();
            user.setUsername(registrationDTO.getUsername());
            user.setPassword(registrationDTO.getPassword());
            user.setFullName(registrationDTO.getFullName());
            user.setPhoneNumber(registrationDTO.getPhoneNumber());

            // Use a provided role or default to CITIZEN
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
