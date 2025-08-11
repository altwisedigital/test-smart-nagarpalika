package com.rudra.smart_nagarpalika.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data   
public class WardsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime createdAt;


    // Bidirectional mapping (I want to access employees from wards)
    @ManyToMany(mappedBy = "wards", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<EmployeeModel> employees;



}
