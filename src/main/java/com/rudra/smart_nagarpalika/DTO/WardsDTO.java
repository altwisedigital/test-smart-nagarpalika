package com.rudra.smart_nagarpalika.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WardsDTO {
    private long id;
    private String name;
    private LocalDateTime createdAt;
}
