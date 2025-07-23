package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends JpaRepository<EmployeeModel, Long> {

    // Optional: Add custom queries if needed
    boolean existsByMobile(String mobile); // Example: check if employee already exists

}
