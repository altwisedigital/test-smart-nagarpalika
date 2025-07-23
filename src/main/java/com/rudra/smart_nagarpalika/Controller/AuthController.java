package com.rudra.smart_nagarpalika.Controller;


import com.rudra.smart_nagarpalika.DTO.LoginRequestDTO;
import com.rudra.smart_nagarpalika.DTO.UserRegistrationDTO;
import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import com.rudra.smart_nagarpalika.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserServices userService;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            boolean roleAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (!roleAdmin) {
                return ResponseEntity.status(403).body(new ErrorResponse("Access Denied: Not an admin"));
            }

            return ResponseEntity.ok().body(new LoginResponse("Successfully logged in", authentication.getName()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid username or password"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Authentication failed: " + e.getMessage()));
        }
    }

    // Response DTOs
    public record LoginResponse(String message, String username) {}
    public record ErrorResponse(String error) {}


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
