package com.example.VideoService.service.impl;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.VideoService.dto.AuthenticatedUserDto;
import com.example.VideoService.feign.AuthClient;
import com.example.VideoService.model.Video;
import com.example.VideoService.repository.VideoRepository;
import com.example.VideoService.service.VideoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final AuthClient authClient;
    private final Path storagePath = Paths.get("videos");

    @Override
    public void saveVideo(MultipartFile file, String title, String desc, String tags, String status, String token) {
        try {
            Files.createDirectories(storagePath);

            String originalName = file.getOriginalFilename();
            String sanitizedName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            String filename = "vid_" + System.currentTimeMillis() + "_" + sanitizedName;

            Path targetPath = storagePath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Long uploaderId = authClient.getCurrentUser(token).id();

            Video video = new Video(null, title, desc, targetPath.toString(), null, tags, status,
                    uploaderId, LocalDateTime.now(), null);
            videoRepository.save(video);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save video", e);
        }
    }

    @Override
    public Optional<Video> getVideoById(Long id) {
        return videoRepository.findById(id);
    }

    @Override
    public List<Video> getVideosByUploader(Long uploaderId) {
        return videoRepository.findByUploaderId(uploaderId);
    }

    @Override
    public List<Video> getAllPublicVideos() {
        return videoRepository.findByStatus("PUBLIC");
    }
}
