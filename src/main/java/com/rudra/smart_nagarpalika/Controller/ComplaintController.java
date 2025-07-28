package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.ComplaintRequestDTO;
import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.Model.UserModel;
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


    @PostMapping(value = "/register-with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> registerComplaintWithImages(
            @RequestParam String username,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String latitude,
            @RequestParam String longitude,
            @RequestParam String location,
            @RequestPart("images") List<MultipartFile> imageFiles
    ) {
        try {
            Optional<UserModel> userOptional = userRepo.findByUsername(username.trim());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            double lat = Double.parseDouble(latitude.trim());
            double lng = Double.parseDouble(longitude.trim());

            ComplaintRequestDTO dto = new ComplaintRequestDTO();
//            dto.setUsername(username.trim());
            dto.setDescription(description.trim());
            dto.setCategory(category.trim());
            dto.setLocation(location.trim());
            dto.setLatitude(lat);
            dto.setLongitude(lng);

            String message = complaintService.saveComplaint(dto, userOptional.get(), imageFiles);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid latitude or longitude");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
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