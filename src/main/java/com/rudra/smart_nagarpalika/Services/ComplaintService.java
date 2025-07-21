package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.ComplaintRequestDTO;
import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Model.ComplaintStatus;
import com.rudra.smart_nagarpalika.Model.UserModel;
import com.rudra.smart_nagarpalika.Repository.ComplaintRepo;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {

    private final ComplaintRepo complaintRepo;


    private final ImageService imageService;

    public String saveComplaint(ComplaintRequestDTO dto, UserModel user, List<MultipartFile> imageFiles) {
        List<String> uploadedPaths = new ArrayList<>();

        for (MultipartFile file : imageFiles) {
            if (!file.isEmpty()) {
                try {
                    String path = imageService.saveImage(file);
                    uploadedPaths.add(path);
                    System.out.println("this is the  current path we are using"+ path);

                } catch (IOException e) {
                    log.error("Failed to save image: {}", file.getOriginalFilename(), e);
                    throw new RuntimeException("Image upload failed: " + file.getOriginalFilename());
                }
            }
        }

        if (uploadedPaths.isEmpty()) {
            throw new RuntimeException("No valid images were uploaded");
        }

        ComplaintModel complaint = new ComplaintModel();
        complaint.setDescription(dto.getDescription());
        complaint.setCategory(dto.getCategory());
        complaint.setLocation(dto.getLocation());
        complaint.setLatitude(dto.getLatitude());
        complaint.setLongitude(dto.getLongitude());
        complaint.setImageUrls(uploadedPaths);
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.Pending);
        complaint.setSubmittedBy(user.getUsername());
        complaint.setUser(user);

        complaintRepo.save(complaint);
        return "Complaint registered successfully";
    }





    public List<String> getImageUrlsByUserId(Long userId) {
        List<ComplaintModel> complaints = complaintRepo.findByUserId(userId);

        List<String> allImageUrls = new ArrayList<>();
        for (ComplaintModel complaint : complaints) {
            if (complaint.getImageUrls() != null) {
                allImageUrls.addAll(complaint.getImageUrls());
            }
        }

        return allImageUrls;
    }

}

