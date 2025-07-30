package com.rudra.smart_nagarpalika.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WardsDTO {

    private String name;
    private LocalDateTime createdAt;
}
