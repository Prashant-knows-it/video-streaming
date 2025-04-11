package com.example.VideoService.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "videos")
public class Video {
    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Video path is required")
    private String videoPath;

    private String thumbnailPath;

    private String tags;

    @Column(nullable = false)
    private String status = "PUBLIC";

    @Column(nullable = false)
    private Long uploaderId;

    private LocalDateTime uploadDate;

    private Long durationInSeconds;
}
