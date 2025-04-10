package com.example.VideoService.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String tags,
            @RequestParam String status,
            @RequestHeader("Authorization") String token
    ) {
        videoService.saveVideo(file, title, description, tags, status, token);
        return ResponseEntity.ok("Video uploaded successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        return ResponseEntity.of(videoService.getVideoById(id));
    }

    @GetMapping("/uploader/{uploaderId}")
    public List<Video> getVideosByUploader(@PathVariable Long uploaderId) {
        return videoService.getVideosByUploader(uploaderId);
    }

    @GetMapping("/public")
    public List<Video> getPublicVideos() {
        return videoService.getAllPublicVideos();
    }
}
