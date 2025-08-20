package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Services.EmployeeService;
import com.rudra.smart_nagarpalika.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("employee")
@RequiredArgsConstructor
public class EmployeeController {


    private  final EmployeeService employeeService;




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










