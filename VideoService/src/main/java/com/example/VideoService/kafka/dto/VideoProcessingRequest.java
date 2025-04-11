package com.example.VideoService.kafka.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoProcessingRequest {
    private String videoId;
    private String originalPath;
    private String hlsOutputDir;
}
