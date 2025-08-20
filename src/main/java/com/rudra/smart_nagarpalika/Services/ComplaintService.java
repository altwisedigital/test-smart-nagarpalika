package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.ComplaintRequestDTO;
import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.Model.*;
import com.rudra.smart_nagarpalika.Repository.ComplaintRepo;
import com.rudra.smart_nagarpalika.Repository.DepartmentRepo;
import com.rudra.smart_nagarpalika.Repository.EmployeeRepo;
import com.rudra.smart_nagarpalika.Repository.WardsRepo;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {

    private final ComplaintRepo complaintRepo;
    private  final DepartmentRepo departmentRepo;
    private final WardsRepo wardsRepo;
    private final EmployeeRepo employeeRepo;
    private final ImageService imageService;

    public String saveComplaint(ComplaintRequestDTO dto, UserModel user, List<MultipartFile> imageFiles) {
        List<String> uploadedPaths = new ArrayList<>();

        // 1. Save image files
        for (MultipartFile file : imageFiles) {
            if (!file.isEmpty()) {
                try {
                    String path = imageService.saveImage(file);
                    uploadedPaths.add(path);
                } catch (IOException e) {
                    log.error("Failed to save image: {}", file.getOriginalFilename(), e);
                    throw new RuntimeException("Image upload failed: " + file.getOriginalFilename());
                }
            }
        }

        // 2. Validate image upload
        if (uploadedPaths.isEmpty()) {
            throw new RuntimeException("No valid images were uploaded");
        }

        // 3. Create complaint
        ComplaintModel complaint = new ComplaintModel();
        complaint.setDescription(dto.getDescription());
        complaint.setDepartment(dto.getDepartment());
        complaint.setLocation(dto.getLocation());
        complaint.setWard(dto.getWards());
        complaint.setLatitude(dto.getLatitude());
        complaint.setLongitude(dto.getLongitude());

         // saving images in proper table abd providing its id as mapped to the complaint list
        List<ImageByUserModel> ImageBYUsers = uploadedPaths.stream().map(url -> {
            ImageByUserModel img = new ImageByUserModel();
            img.setImageUrl(url);
            img.setComplaint(complaint); // set the FK
            return img;
        }).toList();

        complaint.setImages(ImageBYUsers);

        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.InProgress);
        complaint.setSubmittedBy(user.getUsername());
        complaint.setUser(user);

        /// 4. Auto-assign employee based on category as well as we assign
        /// the complaint to all the employees from the same wards
          // Steps 1: get department and ward from the complaint

        String departmentName = dto.getDepartment().getName();
//        String wardsName = dto.getWards().getName();

        DepartmentModel dept = departmentRepo.findByName(departmentName);

        if (dept == null) {
            throw new RuntimeException("Department not found: " + departmentName);
        }
       // step2:  fetch the list employees according to that department
        List<EmployeeModel> employees = employeeRepo.findByDepartment(dept);
        //step3: filter the  certain employees assigned to the ward from the complaint
        Optional<EmployeeModel> matchingEmployee = employeeRepo.findAll().stream()
                .filter(emp -> emp.getWards().stream()
                        .anyMatch(ward -> ward.getId().equals(dto.getWards().getId())))
                .findFirst();


         //step4: assign if found
        matchingEmployee.ifPresent(employee -> {
            // Set ManyToOne side
            complaint.setAssignedEmployee(employee);

            // Set ManyToMany side
            employee.getAssignedComplaints().add(complaint);
        });



        // 5. Save complaint
        complaintRepo.save(complaint);
        return "Complaint registered successfully";
    }



    private Departments mapCategoryToDepartment(String category) {
        switch (category.toLowerCase()) {
            case "drainage":
                return Departments.DRAINAGE_MAINTENANCE;
            case "road":
                return Departments.ROAD_MAINTENANCE;
            case "water":
                return Departments.WATER_MAINTENANCE;
            case "light":
                return Departments.LIGHT_MAINTENANCE;
            default:
                return Departments.OTHER;
        }
    }








    public List<ComplaintResponseDTO> getAllComplaints() {
        List<ComplaintModel> complaints = complaintRepo.findAll();
        //calling the local ip to use in image


            return complaints.stream().map(complaint -> new ComplaintResponseDTO(
                    complaint.getId(),
                    complaint.getDescription(),
                    complaint.getDepartment() != null ? complaint.getDepartment().getName() : null,
                    complaint.getWard() != null ? complaint.getWard().getName()  : null,
                    complaint.getLocation(),
                    complaint.getImages() != null
                            ? complaint.getImages().stream()
                                .map(
                                        img -> "http://" + IpServices.getCurrentIP() + ":8080/uploads/citizen_image_uploads/" + img.getImageUrl()
                                        ///  calling the local io address as we fetch the complaints for testing purpose
                                )
                                .toList()
                            : List.of(),
                    complaint.getSubmittedBy(),
                    complaint.getStatus().name(),
                    complaint.getAssignedEmployee() != null
                            ? complaint.getAssignedEmployee().getFirstName()   + " " + complaint.getAssignedEmployee().getLastName()
                            : null,
                    complaint.getCreatedAt()
            )).toList();
    }


        public  List<ComplaintResponseDTO> getComplaintsByUsername(String  username){
         List<ComplaintModel> byUsername = complaintRepo.findBySubmittedBy(username);

         return byUsername.stream()
                 .map(ComplaintResponseDTO::new).toList();

     }



}

