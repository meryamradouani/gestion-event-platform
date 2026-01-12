package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "profile_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType; // e.g., "image/jpeg", "image/png"

    public ProfileImage(Long profileId, byte[] imageData, String fileName, String fileType) {
        this.profileId = profileId;
        this.imageData = imageData;
        this.fileName = fileName;
        this.fileType = fileType;
    }
}
