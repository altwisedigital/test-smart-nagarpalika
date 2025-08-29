package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Services.EmployeeService;
import com.rudra.smart_nagarpalika.Services.UserServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {


    private  final EmployeeService employeeService;

    @PostMapping("/complaints/{id}/complete")
    public ResponseEntity<?> completeComplaint(
            @PathVariable Long id,
            @RequestParam("repairDescription") String repairDescription,
            @RequestParam("images") List<MultipartFile> imageFiles
    ) { 
        try {
            log.info("Employee updating complaint {} with remarks and {} images", id, imageFiles.size());

            String response = employeeService.completeComplaint(id, repairDescription, imageFiles);

            log.info("Complaint {} updated successfully", id);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Business error while updating complaint {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error while updating complaint {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while updating complaint.");
        }
    }



    @GetMapping("assignedComplaints/{name}")
    public ResponseEntity<?> fetchAssignedComplaint(@PathVariable String name) {
        try {
            List<ComplaintResponseDTO> assignedComplaint = employeeService.fetchAssignedComplaint(name);

            if (assignedComplaint.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Collections.singletonMap("message", "No data found for the user: " + name));
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Here is the list of assigned complaints for user: " + name);
            response.put("count", assignedComplaint.size());
            response.put("data", assignedComplaint);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> errorResponse = Map.of(
                    "message", "Error fetching complaints",
                    "error", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }






}










