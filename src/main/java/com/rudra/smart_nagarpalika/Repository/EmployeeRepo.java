package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.Departments;
import com.rudra.smart_nagarpalika.Model.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<EmployeeModel, Long> {

    // Optional: Add custom queries if needed
    boolean existsByMobile(String mobile); // Example: check if employee already exists
    Optional<EmployeeModel> findByMobile(String mobile);
    void deleteByMobile(String mobile);
    List<EmployeeModel> findByDepartment(Departments departments);


}
