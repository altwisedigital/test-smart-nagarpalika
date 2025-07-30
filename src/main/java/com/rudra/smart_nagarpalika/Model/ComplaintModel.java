package com.rudra.smart_nagarpalika.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ComplaintModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    @ManyToOne
    @JoinColumn(name = "department_name")
    private DepartmentModel department;

    private String location;

//    @ElementCollection
@OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ImageModel> images = new ArrayList<>();


    private LocalDateTime createdAt;
    private String submittedBy;
    @Column
    private double latitude;

    @Column
    private double longitude;
    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;


    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeModel assignedEmployee;

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private WardsModel ward;

}
