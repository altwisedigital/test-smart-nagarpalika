package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.DepartmentDTO;
import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import com.rudra.smart_nagarpalika.Repository.DepartmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeparmentService {
    final private DepartmentRepo departmentRepo;

 // crate department
    public void createDepartment(DepartmentDTO dto){
       DepartmentModel department = new DepartmentModel();
       department.setName(dto.getName());
       department.setCreatedAt(LocalDateTime.now());
      departmentRepo.save(department);
  }
  
  // get all the department
    
    public List<DepartmentModel> GetAllDepartment(){

        return departmentRepo.findAll();
    }
}
