package com.example.VideoService.service.impl;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.example.VideoService.helper.VideoUtils;
import com.example.VideoService.kafka.dto.VideoProcessingRequest;
import com.example.VideoService.kafka.producer.VideoProcessingProducer;
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
    private final VideoProcessingProducer videoProcessingProducer;


    private static String generateVideoId() {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(UUID.randomUUID().toString().getBytes())
                .substring(0, 16);
    }

    @Override
    public void saveVideo(MultipartFile file, String title, String desc, String tags, String status, String token) {
        try {
            Long uploaderId = authClient.getCurrentUser(token).id();

            // Use a proper timestamp format, e.g., "2025-04-11T18-23-00-123"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss-SSS");
            String timestamp = LocalDateTime.now().format(formatter);

            // Extract extension from original filename
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                // Fallback extension if not provided (you might validate further)
                ext = ".mp4";
            }

            // Generate unique video id (using your preferred method, e.g., Base64 encoded UUID)
            String videoId = generateVideoId();

            // Temporary storage directory
            Path tempPath = Paths.get("videos", "temp", timestamp + ext);
            Files.createDirectories(tempPath.getParent());
            Files.copy(file.getInputStream(), tempPath, StandardCopyOption.REPLACE_EXISTING);

            // Create directories for originals, thumbnails, and HLS output
            Path videoDir = Paths.get("videos", "originals", videoId);
            Path thumbDir = Paths.get("videos", "thumbnails", videoId);
            Path hlsDir = Paths.get("videos", "hls", videoId);
            Files.createDirectories(videoDir);
            Files.createDirectories(thumbDir);
            Files.createDirectories(hlsDir);

            // Move the temporary file to the final destination with proper extension
            Path finalVideoPath = videoDir.resolve(timestamp + ext);
            Files.move(tempPath, finalVideoPath, StandardCopyOption.REPLACE_EXISTING);

            // Extract thumbnail
            Path thumbnailPath = thumbDir.resolve("thumb_" + timestamp + ".jpg");
            String thumbnail = VideoUtils.extractThumbnail(finalVideoPath, thumbnailPath);

            // Create Video entity (duration extracted from final file)
            Video video = new Video(videoId, title, desc,
                    finalVideoPath.toString(),
                    thumbnail,
                    tags,
                    status,
                    uploaderId,
                    LocalDateTime.now(),
                    VideoUtils.extractDuration(finalVideoPath));

            videoRepository.save(video);

            // Triggers Kafka
            VideoProcessingRequest request = new VideoProcessingRequest(
                    videoId,
                    finalVideoPath.toString(),
                    hlsDir.toString()
            );
            videoProcessingProducer.send(request);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save video", e);
        }
    }

    @Override
    public Optional<Video> getVideoById(String id) {
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
