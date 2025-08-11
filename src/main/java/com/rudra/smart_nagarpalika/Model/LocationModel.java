package com.rudra.smart_nagarpalika.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LocationModel {
   @Id
   private  int  id;

   private String name;
    private long lat;
    private long lng;
    private String address;
    private  String type;
}
