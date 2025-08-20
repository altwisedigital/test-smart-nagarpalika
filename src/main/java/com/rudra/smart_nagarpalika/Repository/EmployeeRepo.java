package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import com.rudra.smart_nagarpalika.Model.EmployeeModel;
import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.WardsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmployeeRepo extends JpaRepository<EmployeeModel,Long> {

    // In EmployeeRepo
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<EmployeeModel> findByUserAccount(UserModel userAccount);
    Optional<EmployeeModel> findByUserAccount_Username(String username);

    EmployeeModel findByfirstName(String name);

    List<EmployeeModel> findByDepartment(DepartmentModel dept);
}
