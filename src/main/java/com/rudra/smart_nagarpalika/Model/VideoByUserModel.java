package com.rudra.smart_nagarpalika.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_user")
public class VideoByUserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
     private String videoUrl;

     @ManyToOne(fetch =   FetchType.LAZY)
     @JoinColumn(name = "complaint_Id")
     private ComplaintModel complaint;
}
