package com.example.VideoService.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String videoPath; // local storage path
    private String thumbnailPath;
    private String tags; // comma-separated for now
    private String status; // PUBLIC / PRIVATE
    private Long uploaderId;
    private LocalDateTime uploadDate;
    private Long durationInSeconds;
}
