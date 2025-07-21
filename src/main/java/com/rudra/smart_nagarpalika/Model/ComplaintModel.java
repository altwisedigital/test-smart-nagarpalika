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
    private String category;
    private String location;

//    @ElementCollection
@Column(columnDefinition = "TEXT")
    private List<String> imageUrls = new ArrayList<>();

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
}
