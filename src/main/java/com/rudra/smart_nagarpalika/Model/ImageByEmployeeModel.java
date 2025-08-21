package com.rudra.smart_nagarpalika.Model;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ImageByEmployeeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    private ComplaintModel complaint;
}
