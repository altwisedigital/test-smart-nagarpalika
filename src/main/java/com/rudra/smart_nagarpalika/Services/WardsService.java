package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.WardsDTO;
import com.rudra.smart_nagarpalika.Model.WardsModel;
import com.rudra.smart_nagarpalika.Repository.WardsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WardsService {

    private final WardsRepo wardsRepo;


    //add all wards
    public void addAllWards(List<WardsModel> wardsList) {
        wardsList.forEach(ward -> ward.setCreatedAt(LocalDateTime.now())); // optional: set server time
        wardsRepo.saveAll(wardsList);
    }


    // create the wards
    public void createWards(WardsDTO dto){
        WardsModel wards = new WardsModel();

        wards.setName(dto.getName());
        wards.setCreatedAt(LocalDateTime.now());

        wardsRepo.save(wards);

    }

    // fetch all the wards
    public List<WardsModel> GetAllWards(){
        return wardsRepo.findAll();
    }
}
