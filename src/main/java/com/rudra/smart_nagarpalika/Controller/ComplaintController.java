package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.ComplaintRequestDTO;
import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.Model.DepartmentModel;
import com.rudra.smart_nagarpalika.Model.EmployeeModel;
import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Model.WardsModel;
import com.rudra.smart_nagarpalika.Repository.EmployeeRepo;
import com.rudra.smart_nagarpalika.Repository.UserRepo;
import com.rudra.smart_nagarpalika.Services.ComplaintService;
import com.rudra.smart_nagarpalika.Services.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("complaints")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserRepo userRepo;
    private final ImageService imageService;

    private  final EmployeeRepo  employeeRepo;


    @PostMapping(value = "/register-with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> registerComplaintWithImages(
        @RequestParam String username,
        @RequestParam String description,
        @RequestParam DepartmentModel department,
        @RequestParam WardsModel wards,
        @RequestParam String latitude,
        @RequestParam String longitude,
        @RequestParam String location,
        @RequestPart("images") List<MultipartFile> imageFiles
) {
    log.info("=== COMPLAINT REGISTRATION START ===");
    log.info("Received complaint registration request from user: {}", username);
    log.info("Description: {}", description);
    log.info("Department: {}", department);
    log.info("Ward: {}", wards);
    log.info("Latitude: {}, Longitude: {}", latitude, longitude);
    log.info("Location: {}", location);
    log.info("Number of attached images: {}", imageFiles != null ? imageFiles.size() : 0);

    // Step 1: Validate and find user
    try {
        log.info("STEP 1: Finding user by username: {}", username);
        Optional<UserModel> userOptional = userRepo.findByUsername(username.trim());
        if (userOptional.isEmpty()) {
            log.error("STEP 1 FAILED: User not found for username: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        log.info("STEP 1 SUCCESS: User found: {}", userOptional.get().getUsername());

        // Step 2: Parse coordinates
        log.info("STEP 2: Parsing coordinates");
        double lat, lng;
        try {
            lat = Double.parseDouble(latitude.trim());
            lng = Double.parseDouble(longitude.trim());
            log.info("STEP 2 SUCCESS: Parsed coordinates - lat: {}, lng: {}", lat, lng);
        } catch (NumberFormatException e) {
            log.error("STEP 2 FAILED: Invalid coordinates - latitude: '{}', longitude: '{}'. Error: {}", 
                     latitude, longitude, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid latitude or longitude: " + e.getMessage());
        }

        // Step 3: Validate image files
        log.info("STEP 3: Validating image files");
        if (imageFiles == null || imageFiles.isEmpty()) {
            log.warn("STEP 3 WARNING: No image files provided");
        } else {
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                log.info("STEP 3: Image {} - Name: '{}', Size: {} bytes, ContentType: '{}'", 
                        i + 1, file.getOriginalFilename(), file.getSize(), file.getContentType());
                
                if (file.isEmpty()) {
                    log.error("STEP 3 FAILED: Image {} is empty", i + 1);
                    return ResponseEntity.badRequest().body("Image " + (i + 1) + " is empty");
                }
            }
            log.info("STEP 3 SUCCESS: All {} images validated", imageFiles.size());
        }

        // Step 4: Create DTO
        log.info("STEP 4: Creating ComplaintRequestDTO");
        ComplaintRequestDTO dto = new ComplaintRequestDTO();
        dto.setDescription(description.trim());
        dto.setDepartment(department);
        dto.setWards(wards);
        dto.setLocation(location.trim());
        dto.setLatitude(lat);
        dto.setLongitude(lng);
        log.info("STEP 4 SUCCESS: ComplaintRequestDTO created: {}", dto);

        // Step 5: Save complaint (this is where the failure likely occurs)
        log.info("STEP 5: Calling complaintService.saveComplaint()");
        log.info("STEP 5: About to process {} images", imageFiles != null ? imageFiles.size() : 0);
        
        String message;
        try {
            message = complaintService.saveComplaint(dto, userOptional.get(), imageFiles);
            log.info("STEP 5 SUCCESS: Complaint saved successfully: {}", message);
        } catch (Exception serviceException) {
            log.error("STEP 5 FAILED: Error in complaintService.saveComplaint()");
            log.error("STEP 5 ERROR TYPE: {}", serviceException.getClass().getSimpleName());
            log.error("STEP 5 ERROR MESSAGE: {}", serviceException.getMessage());
            log.error("STEP 5 FULL STACK TRACE: ", serviceException);
            
            // Return more specific error based on exception type
            if (serviceException.getMessage() != null) {
                if (serviceException.getMessage().contains("Supabase") || 
                    serviceException.getMessage().contains("storage") ||
                    serviceException.getMessage().contains("bucket")) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Storage service error: " + serviceException.getMessage());
                }
                if (serviceException.getMessage().contains("network") || 
                    serviceException.getMessage().contains("connection")) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Network connectivity error: " + serviceException.getMessage());
                }
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Service error: " + serviceException.getMessage());
        }

        log.info("=== COMPLAINT REGISTRATION SUCCESS ===");
        return ResponseEntity.status(HttpStatus.CREATED).body(message);

    } catch (Exception generalException) {
        log.error("=== COMPLAINT REGISTRATION FAILED ===");
        log.error("UNEXPECTED ERROR TYPE: {}", generalException.getClass().getSimpleName());
        log.error("UNEXPECTED ERROR MESSAGE: {}", generalException.getMessage());
        log.error("UNEXPECTED FULL STACK TRACE: ", generalException);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error: " + generalException.getMessage());
    }
}


    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'USER')")
    public ResponseEntity<List<ComplaintResponseDTO>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }


//
//    @GetMapping("/allImages/{Userid}")
//    public List<String> getImages(@PathVariable Long Userid){
//        return complaintService.getImageUrlsByUserId(Userid);
//    }
}
