package com.rudra.smart_nagarpalika.Services;

import com.rudra.smart_nagarpalika.DTO.AlertRequestDto;
import com.rudra.smart_nagarpalika.DTO.AlertResponseDTO;
import com.rudra.smart_nagarpalika.DTO.ComplaintResponseDTO;
import com.rudra.smart_nagarpalika.Model.AlertsModel;
import com.rudra.smart_nagarpalika.Repository.AlertRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertsService {

     private final AlertRepo alertRepo;
     private final ImageService imageService;

  // creating alerts using
  // Fixed Service Method
  public AlertsModel saveAlert(AlertRequestDto dto, MultipartFile image){
       String uploadedImage = "";

       if ( image != null){
           try{
               String path = imageService.saveAlertImage(image);
                uploadedImage = path;
           } catch (IOException e) {
               log.error("Failed to save image: {}", image.getOriginalFilename(), e);
               throw new RuntimeException("Image upload failed: " + image.getOriginalFilename());
           }
       }

       //validating image upload
      if (uploadedImage.isEmpty()){
          throw new RuntimeException("No valid image were uploaded");
      }
      AlertsModel alerts = new AlertsModel();
      alerts.setTitle(dto.getTitle());
      alerts.setType(dto.getType());
      alerts.setDescription(dto.getDescription());
      alerts.setCreatedAt(LocalDateTime.now());

      alerts.setImageUrl(uploadedImage);



      return  alertRepo.save(alerts);
  }



    // get a single alert by id
     public AlertsModel getAlertById(Long id){
       return alertRepo.getReferenceById(id);
     }

    // get all alerts
    public List<AlertResponseDTO> getAlerts(){
      List<AlertsModel> allAlerts =  alertRepo.findAll();
        return allAlerts.stream()
                .map(AlertResponseDTO::new) // constructor reference
                .toList();
    }

    public List<AlertsModel> getALlALerts(){
      return alertRepo.findAll();
    }
    //Delete alerts by id
    public Boolean DeleteAlert(Long id){
        if (id == null){
            throw new  IllegalArgumentException("can not find id");
        }

        if (alertRepo.existsById(id)){
            alertRepo.deleteById(id);
            return  true;
        }
        return  false;
    }
}
