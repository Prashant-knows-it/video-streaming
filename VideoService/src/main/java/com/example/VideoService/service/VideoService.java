package com.example.VideoService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import com.example.VideoService.model.Video;

public interface VideoService {
    void saveVideo(MultipartFile file, String title, String desc, String tags, String status, String token);
    Optional<Video> getVideoById(String id);
    List<Video> getVideosByUploader(Long uploaderId);
    List<Video> getAllPublicVideos();
}
