package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Find locations by category
    List<Location> findByCategoryName(String categoryName);

    // Find locations within a radius (using Haversine formula)
    @Query(value = """
        SELECT l.* FROM locations l 
        WHERE (6371 * acos(cos(radians(:userLat)) * cos(radians(l.latitude)) * 
        cos(radians(l.longitude) - radians(:userLng)) + sin(radians(:userLat)) * 
        sin(radians(l.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:userLat)) * cos(radians(l.latitude)) * 
        cos(radians(l.longitude) - radians(:userLng)) + sin(radians(:userLat)) * 
        sin(radians(l.latitude))))
        """, nativeQuery = true)
    List<Location> findLocationsWithinRadius(
            @Param("userLat") Double userLat,
            @Param("userLng") Double userLng,
            @Param("radiusKm") Double radiusKm
    );

    // Find by city/area
    List<Location> findByAddressContainingIgnoreCase(String area);
}