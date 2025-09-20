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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service class for managing categories in the Smart Nagarpalika application.
 * Provides methods for creating, retrieving, updating, and deleting categories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepo categoryRepository;
    private final ImageService imageService;

    /**
     * Creates a new category with an optional logo image.
     *
     * @param request The CategoryRequest DTO containing category details.
     * @param image   The optional image file for the category logo.
     * @return A success message with the created category details.
     * @throws IllegalArgumentException If the request is invalid or missing required fields.
     * @throws RuntimeException         If image upload fails.
     */
    public String createCategory(CategoryRequest request, MultipartFile image) {
        // Validate input request
        if (Objects.isNull(request) || isInvalidRequest(request)) {
            log.error("Invalid CategoryRequest provided: {}", request);
            throw new IllegalArgumentException("Category request data is invalid or missing required fields");
        }

        String uploadedLogo = "";
        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
            try {
                uploadedLogo = imageService.saveCategoryLogo(image);
                log.info("Category logo uploaded successfully: {}", uploadedLogo);
            } catch (IOException e) {
                log.error("Failed to save image: {}", image.getOriginalFilename(), e);
                throw new RuntimeException("Failed to upload category logo: " + image.getOriginalFilename(), e);
            }
        }

        // Create and populate Category entity
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setImageUrl(uploadedLogo);

        // Save category to repository
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return "Category created successfully: " + savedCategory;
    }

    /**
     * Validates the CategoryRequest DTO for required fields.
     *
     * @param request The CategoryRequest to validate.
     * @return True if the request is invalid, false otherwise.
     */
    private boolean isInvalidRequest(CategoryRequest request) {
        return request.getName() == null || request.getName().trim().isEmpty();
    }

    /**
     * Retrieves all categories and maps them to CategoryResponse DTOs.
     *
     * @return A list of CategoryResponse DTOs representing all categories.
     */
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        log.info("Retrieved {} categories", categories.size());
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Category entity to a CategoryResponse DTO.
     *
     * @param category The Category entity to convert.
     * @return The mapped CategoryResponse DTO.
     */
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(buildImageUrl(category.getImageUrl()))
                .locations(category.getLocations() != null ?
                        category.getLocations().stream()
                                .map(Location::getName)
                                .collect(Collectors.toList()) :
                        List.of())
                .build();
    }

    /**
     * Builds the complete image URL for a category logo.
     *
     * @param imageUrl The stored image URL or path.
     * @return The complete image URL or empty string if imageUrl is null/empty.
     */
    private String buildImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "";
        }
        return  imageUrl;
    }

    /**
     * Updates an existing category with new details and an optional logo image.
     *
     * @param id      The ID of the category to update.
     * @param request The CategoryRequest DTO containing updated details.
     * @param image   The optional new logo image.
     * @return The updated CategoryResponse DTO.
     * @throws IllegalArgumentException If the ID is null or request is invalid.
     * @throws RuntimeException         If the category is not found or image upload fails.
     */
    public CategoryResponse updateCategory(Long id, CategoryRequest request, MultipartFile image) throws IOException {
        if (id == null) {
            log.error("Null ID provided for updateCategory");
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        if (Objects.isNull(request) || isInvalidRequest(request)) {
            log.error("Invalid CategoryRequest for update: {}", request);
            throw new IllegalArgumentException("Category request data is invalid or missing required fields");
        }

        // Retrieve existing category
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category not found for ID: {}", id);
                    return new RuntimeException("Category not found with ID: " + id);
                });

        // Update category fields
        category.setName(request.getName().trim());

        // Handle image update if provided
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = imageService.saveCategoryLogo(image);
                category.setImageUrl(imageUrl);
                log.info("Category logo updated for ID: {}", id);
            } catch (IOException e) {
                log.error("Failed to update image for category ID {}: {}", id, image.getOriginalFilename(), e);
                throw new RuntimeException("Failed to upload category logo: " + image.getOriginalFilename(), e);
            }
        }

        // Save updated category
        Category updated = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", id);
        return toResponse(updated);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     * @throws IllegalArgumentException If the ID is null.
     * @throws RuntimeException         If the category is not found.
     */
    public void deleteCategory(Long id) {
        if (id == null) {
            log.error("Null ID provided for deleteCategory");
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        if (!categoryRepository.existsById(id)) {
            log.error("Category not found for ID: {}", id);
            throw new RuntimeException("Category not found with ID: " + id);
        }

        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with ID: {}", id);
    }
}