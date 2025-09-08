// LocationService.java
package com.rudra.smart_nagarpalika.Services;


import com.rudra.smart_nagarpalika.DTO.LocationRequest;
import com.rudra.smart_nagarpalika.DTO.LocationResponse;
import com.rudra.smart_nagarpalika.Model.Category;
import com.rudra.smart_nagarpalika.Model.Location;
import com.rudra.smart_nagarpalika.Repository.CategoryRepo;
import com.rudra.smart_nagarpalika.Repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final CategoryRepo categoryRepository;

    public LocationResponse createLocation(LocationRequest request) {
        try {
            // Find category
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Create Location
            Location location = new Location();
            location.setName(request.getName());
            location.setAddress(request.getAddress());
            location.setLatitude(request.getLatitude());
            location.setLongitude(request.getLongitude());
            location.setCategory(category);

            // Save to DB
            Location saved = locationRepository.save(location);

            return toResponse(saved);

        } catch (RuntimeException e) {
            // Handles custom thrown exceptions like "Category not found"
            throw e;
        } catch (Exception e) {
            // Handles any unexpected errors
            throw new RuntimeException("Failed to create location: " + e.getMessage(), e);
        }
    }


    public List<LocationResponse> getAllLocations() {
        return locationRepository.findAll()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    private LocationResponse toResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .categoryName(location.getCategory().getName())
                .build();
    }


    // Update Location
    public LocationResponse updateLocation(Long id, LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setCategory(category);

        Location updated = locationRepository.save(location);
        return toResponse(updated);
    }

    // Delete Location
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Location not found");
        }
        locationRepository.deleteById(id);
    }

}
