package com.example.VideoService.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.VideoService.model.Video;
import com.example.VideoService.service.VideoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<String> uploadVideo(
            @RequestParam MultipartFile file,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "PUBLIC") String status,
            @RequestHeader("Authorization") String token) {
        videoService.saveVideo(file, title, description, tags, status, token);
        return ResponseEntity.ok("Video uploaded successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable String id) {
        return videoService.getVideoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/uploader/{uploaderId}")
    public ResponseEntity<List<Video>> getVideosByUploader(@PathVariable Long uploaderId) {
        return ResponseEntity.ok(videoService.getVideosByUploader(uploaderId));
    }

    @GetMapping("/public")
    public ResponseEntity<List<Video>> getPublicVideos() {
        return ResponseEntity.ok(videoService.getAllPublicVideos());
    }
}
