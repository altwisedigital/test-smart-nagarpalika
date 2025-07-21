package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceCustom implements UserDetailsService {

    private final UserRepo userRepo; // Fixed: added final and proper injection

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fixed: Proper return statement and type casting
        UserModel user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return user; // Assuming UserModel implements UserDetails
    }
}