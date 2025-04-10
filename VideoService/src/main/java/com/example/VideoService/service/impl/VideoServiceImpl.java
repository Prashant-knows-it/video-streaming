package com.example.VideoService.service.impl;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import com.example.VideoService.helper.VideoUtils;
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
            Path videoDir = Paths.get("videos");
            Path thumbDir = Paths.get("thumbnails");

            Files.createDirectories(videoDir);
            Files.createDirectories(thumbDir);

            String originalName = file.getOriginalFilename();
            String sanitizedName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            String filename = "vid_" + System.currentTimeMillis() + "_" + sanitizedName;

            Path videoPath = videoDir.resolve(filename);
            Files.copy(file.getInputStream(), videoPath, StandardCopyOption.REPLACE_EXISTING);

            String thumbName = "thumb_" + filename + ".jpg";
            Path thumbnailPath = thumbDir.resolve(thumbName);

            String thumbnail = VideoUtils.extractThumbnail(videoPath, thumbnailPath);
            long duration = VideoUtils.extractDuration(videoPath);

            Long uploaderId = authClient.getCurrentUser(token).id();

            Video video = new Video(null, title, desc, videoPath.toString(), thumbnail, tags, status,
                    uploaderId, LocalDateTime.now(), duration);
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
