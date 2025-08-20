package com.rudra.smart_nagarpalika.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = true)
    private String password;

    private String fullName;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private EmployeeModel employee;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ComplaintModel> complaints = new ArrayList<>();

    // UserDetails implementation methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can add logic here if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // You can add logic here if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can add logic here if needed
    }

    @Override
    public boolean isEnabled() {
        return true; // You can add logic here if needed
    }
}

