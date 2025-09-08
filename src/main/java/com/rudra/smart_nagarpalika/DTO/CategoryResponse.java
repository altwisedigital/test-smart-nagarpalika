package com.rudra.smart_nagarpalika.DTO;

// CategoryResponse.java


import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private List<String> locations; // only names to avoid deep recursion
}
