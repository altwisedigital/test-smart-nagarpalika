package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {


    Category findByName(String name);
    boolean existsByName(String name);
}