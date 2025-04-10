package com.example.VideoService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.VideoService.model.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByUploaderId(Long uploaderId);
    List<Video> findByStatus(String status);

}
