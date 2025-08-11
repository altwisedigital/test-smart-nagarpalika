package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.ComplaintModel;
import com.rudra.smart_nagarpalika.Model.WardsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardsRepo extends JpaRepository<WardsModel,Long> {
//    List<ComplaintModel> findBySubmittedBy(String submittedBy);
}
