package com.example.StreamingService.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

// http://localhost:8082/stream/watch/{videoID}

@Controller
@RequestMapping("/stream")
public class VideoStreamingController {

    private static final String BASE_PATH = "videos/hls";

    @GetMapping("/{videoId}/master.m3u8")
    public ResponseEntity<Resource> getMasterPlaylist(@PathVariable String videoId) throws Exception {
        return serveFile(Paths.get(BASE_PATH, videoId, "master.m3u8"));
    }

    @GetMapping("/{videoId}/{resolution}/stream.m3u8")
    public ResponseEntity<Resource> getStreamPlaylist(@PathVariable String videoId, @PathVariable String resolution) throws Exception {
        return serveFile(Paths.get(BASE_PATH, videoId, resolution, "stream.m3u8"));
    }

    @GetMapping("/{videoId}/{resolution}/{segment}")
    public ResponseEntity<Resource> getSegment(@PathVariable String videoId, @PathVariable String resolution, @PathVariable String segment) throws Exception {
        return serveFile(Paths.get(BASE_PATH, videoId, resolution, segment));
    }

    private ResponseEntity<Resource> serveFile(Path filePath) throws Exception {
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) throw new RuntimeException("File not found: " + filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/watch/{videoId}")
    public String streamPage(@PathVariable String videoId, Model model) {
        model.addAttribute("videoId", videoId);
        return "player";
    }

}
