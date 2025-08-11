package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepo  extends JpaRepository<DepartmentModel, Long> {
    DepartmentModel findByName(@NotBlank String departmentName);

    // In DepartmentRepo


}
