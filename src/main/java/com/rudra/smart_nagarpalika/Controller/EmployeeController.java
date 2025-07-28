package com.rudra.smart_nagarpalika.Controller;

import com.rudra.smart_nagarpalika.DTO.EmployeeDTO;

import com.rudra.smart_nagarpalika.Services.EmployeeService;
import com.rudra.smart_nagarpalika.Services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("employee")
@RequiredArgsConstructor
public class EmployeeController {


    private final UserServices userServices;
    private final EmployeeService employeeService;






}










