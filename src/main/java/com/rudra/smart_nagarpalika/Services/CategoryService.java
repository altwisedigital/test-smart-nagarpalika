package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.CategoryRequest;
import com.rudra.smart_nagarpalika.DTO.CategoryResponse;
import com.rudra.smart_nagarpalika.Model.Category;
import com.rudra.smart_nagarpalika.Model.Location;
import com.rudra.smart_nagarpalika.Repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

     private final CategoryRepo categoryRepository;
     private final ImageService imageService;

     // Create a new category with optional logo upload
     public String createCategory(CategoryRequest request, MultipartFile image) {
        String uploadedLogo= "";
        if ( image != null){
             try {
                  String path = imageService.saveCategoryLogo(image);
                  uploadedLogo = path;
             } catch (IOException e) {
                  log.error("Failed to save image: {}", image.getOriginalFilename(), e);
                  throw new RuntimeException("Image upload failed: " + image.getOriginalFilename());
             }

        }
        // validating image upload
          if (uploadedLogo.isEmpty()){
               throw new RuntimeException("No valid image were uploaded");
          }
          Category category = new Category();
          category.setName(request.getName());
          category.setImageUrl(uploadedLogo);
          categoryRepository.save(category);
          return "Category  created  successfully"+ category;
     }

     // Get a list of all categories
     public List<CategoryResponse> getAllCategories() {
          return categoryRepository.findAll()
                  .stream()
                  .map(this::toResponse)
                  .collect(Collectors.toList());
     }

     // Convert entity → response DTO
     private CategoryResponse toResponse(Category category) {
          return CategoryResponse.builder()
                  .id(category.getId())
                  .name(category.getName())
                  .imageUrl(category.getImageUrl())
                  .locations(category.getLocations()
                          .stream()
                          .map(Location::getName)
                          .collect(Collectors.toList()))
                  .build();
     }

     // Update Category
     public CategoryResponse updateCategory(Long id, CategoryRequest request, MultipartFile image) throws IOException {
          Category category = categoryRepository.findById(id)
                  .orElseThrow(() -> new RuntimeException("Category not found"));

          category.setName(request.getName());

          if (image != null && !image.isEmpty()) {
               String imageUrl = imageService.saveCategoryLogo(image);
               category.setImageUrl(imageUrl);
          }

          Category updated = categoryRepository.save(category);
          return toResponse(updated);
     }

     // Delete Category
     public void deleteCategory(Long id) {
          if (!categoryRepository.existsById(id)) {
               throw new RuntimeException("Category not found");
          }
          categoryRepository.deleteById(id);
     }

}
