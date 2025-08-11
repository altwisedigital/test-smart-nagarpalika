package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepo extends JpaRepository<ComplaintModel, Long> {

    List<ComplaintModel> findByUserId(Long userId);


    List<ComplaintModel> findBySubmittedBy(String username);
}
