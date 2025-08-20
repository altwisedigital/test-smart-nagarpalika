package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.LocationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalBodiesRepo extends JpaRepository<LocationModel,Long> {
}
