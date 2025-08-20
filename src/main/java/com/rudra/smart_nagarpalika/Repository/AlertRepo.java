package com.rudra.smart_nagarpalika.Repository;

import com.rudra.smart_nagarpalika.Model.AlertsModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepo extends JpaRepository<AlertsModel,Long> {

}
