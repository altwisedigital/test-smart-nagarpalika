package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.ComplaintRequestDTO;
import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.Model.*;
import com.rudra.smart_nagarpalika.Repository.ComplaintRepo;
import com.rudra.smart_nagarpalika.Repository.EmployeeRepo;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {

    private final ComplaintRepo complaintRepo;

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
        complaint.setCategory(dto.getCategory());
        complaint.setLocation(dto.getLocation());
        complaint.setLatitude(dto.getLatitude());
        complaint.setLongitude(dto.getLongitude());

         // saving images in proper table abd providing its id as mapped to the complaint list
        List<ImageModel> imageModels = uploadedPaths.stream().map(url -> {
            ImageModel img = new ImageModel();
            img.setImageUrl(url);
            img.setComplaint(complaint); // set the FK
            return img;
        }).toList();

        complaint.setImages(imageModels);

        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.Pending);
        complaint.setSubmittedBy(user.getUsername());
        complaint.setUser(user);

        // 4. Auto-assign employee based on category
        Departments dept = mapCategoryToDepartment(dto.getCategory());
        List<EmployeeModel> employees = employeeRepo.findByDepartment(dept);
        if (!employees.isEmpty()) {
            complaint.setAssignedEmployee(employees.get(0)); // First available employee
        }

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




//    public List<String> getImageUrlsByUserId(Long userId) {
//        List<ComplaintModel> complaints = complaintRepo.findByUserId(userId);
//
//        List<String> allImageUrls = new ArrayList<>();
//        for (ComplaintModel complaint : complaints) {
//            if (complaint.getImageUrls() != null) {
//                allImageUrls.addAll(complaint.getImageUrls());
//            }
//        }
//
//        return allImageUrls;
//    }



    public List<ComplaintResponseDTO> getAllComplaints() {
        List<ComplaintModel> complaints = complaintRepo.findAll();

        return complaints.stream().map(complaint -> new ComplaintResponseDTO(
                complaint.getId(),
                complaint.getDescription(),
                complaint.getCategory(),
                complaint.getLocation(),

                complaint.getImages() != null
                        ? complaint.getImages().stream()
                        .map(img -> "http://localhost:8080" + img.getImageUrl())

                .toList()
                        : List.of(),

                complaint.getSubmittedBy(),
                complaint.getStatus().name(),
                complaint.getAssignedEmployee() != null
                        ? complaint.getAssignedEmployee().getFirstname() + " " + complaint.getAssignedEmployee().getLastname()
                        : null,
                complaint.getCreatedAt()
        )).toList();
    }


     public  List<ComplaintResponseDTO> getComplaintsByUsername(String  username){
         List<ComplaintModel> byUsername = complaintRepo.findBySubmittedBy(username);

         return byUsername.stream()
                 .map( complaint  -> new ComplaintResponseDTO(complaint) )
                 .collect(Collectors.toList());
     }

}

