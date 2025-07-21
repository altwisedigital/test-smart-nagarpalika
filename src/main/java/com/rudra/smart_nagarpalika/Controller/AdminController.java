package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationManager authenticationManager;

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
}
