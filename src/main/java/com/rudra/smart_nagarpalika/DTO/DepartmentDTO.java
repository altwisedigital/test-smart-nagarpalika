package com.rudra.smart_nagarpalika.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DepartmentDTO {

    private LocalDateTime createdAt;
    private String name;
}
