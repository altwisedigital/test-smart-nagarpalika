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
        log.info("Received complaint registration request from user: {}", username);
        log.info("Description: {}", description);
        log.info("Department: {}", department);  // Assumes DepartmentModel has toString()
        log.info("Ward: {}", wards);             // Assumes WardsModel has toString()
        log.info("Latitude: {}, Longitude: {}", latitude, longitude);
        log.info("Location: {}", location);
        log.info("Number of attached images: {}", imageFiles.size());

        try {
            Optional<UserModel> userOptional = userRepo.findByUsername(username.trim());
            if (userOptional.isEmpty()) {
                log.warn("User not found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }


            double lat = Double.parseDouble(latitude.trim());
            double lng = Double.parseDouble(longitude.trim());

            log.info("Parsed latitude: {}, longitude: {}", lat, lng);

            ComplaintRequestDTO dto = new ComplaintRequestDTO();
            dto.setDescription(description.trim());
            dto.setDepartment(department);
            dto.setWards(wards);
            dto.setLocation(location.trim());
            dto.setLatitude(lat);

            dto.setLongitude(lng);

            log.info("ComplaintRequestDTO constructed: {}", dto);

            String message = complaintService.saveComplaint(dto, userOptional.get(), imageFiles);
            log.info("Complaint saved successfully: {}", message);

            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (NumberFormatException e) {
            log.error("Invalid latitude or longitude: {}, {}. Error: {}", latitude, longitude, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid latitude or longitude");
        } catch (Exception e) {
            log.error("Error occurred while registering complaint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
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