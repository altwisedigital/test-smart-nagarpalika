package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);

    // Find users by role
    List<UserModel> findByRole(UserRole role);

    // Check if the username exists
    boolean existsByUsername(String username);

    // Find by phone numbera
    Optional<UserModel> findByPhoneNumber(String phoneNumber);


    Optional<UserModel> findByUsernameAndPassword(String username, String password);
    List<UserModel> findByRoleOrderByRole(String role);
//}
}