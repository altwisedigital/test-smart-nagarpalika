package com.rudra.smart_nagarpalika.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstname;
    private String lastname;

    private UserRole role ;

    @Enumerated(EnumType.STRING)
    private Departments department;

    private String mobile;

    @ElementCollection
    private List<Integer> assignedComplaints; //  we have to link this later to Complaint entities
}
