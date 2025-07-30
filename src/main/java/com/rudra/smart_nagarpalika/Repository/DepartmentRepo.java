package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo  extends JpaRepository<DepartmentModel, Long> {
}
