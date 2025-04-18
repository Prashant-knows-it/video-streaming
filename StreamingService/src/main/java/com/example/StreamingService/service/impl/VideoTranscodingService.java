package com.example.StreamingService.service.impl;

import com.example.StreamingService.kafka.dto.VideoProcessingRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class VideoTranscodingService {

    private static final String FFmpeg_PATH = "C:\\ffmpeg\\bin\\ffmpeg.exe"; // Path to your ffmpeg executable

    private static final Map<String, String> resolutionParams = Map.of(
            "360p", "640:360",
            "480p", "854:480",
            "720p", "1280:720"
    );

    private static final Map<String, String> resolutionBitrates = Map.of(
            "360p", "500k",
            "480p", "1000k",
            "720p", "1500k"
    );

    public void transcodeToHLS(VideoProcessingRequest request) throws IOException {
        String originalVideoPath = request.getOriginalPath();
        String hlsOutputDir = request.getHlsOutputDir(); // includes videoId
        String videoId = request.getVideoId();

        List<String> resolutions = new ArrayList<>(resolutionParams.keySet());

        for (String res : resolutions) {
            transcodeToResolution(originalVideoPath, hlsOutputDir, res);
        }

        generateMasterPlaylist(hlsOutputDir, resolutions);

        System.out.println("HLS transcoding and master playlist completed for video: " + videoId);
    }

    private void transcodeToResolution(String originalPath, String outputDir, String resolution) throws IOException {
        Path resDir = Paths.get(outputDir, resolution);
        Files.createDirectories(resDir);

        String outStream = resDir.resolve("stream.m3u8").toString();
        String segmentPattern = resDir.resolve("segment%03d.ts").toString();

        List<String> command = Arrays.asList(
                "cmd.exe", "/c", FFmpeg_PATH,
                "-i", originalPath,
                "-vf", "scale=" + resolutionParams.get(resolution),
                "-c:v", "libx264", "-preset", "fast", "-b:v", resolutionBitrates.get(resolution),
                "-c:a", "aac", "-b:a", "128k",
                "-f", "hls",
                "-hls_time", "10",
                "-hls_list_size", "0",
                "-hls_segment_filename", segmentPattern,
                outStream
        );

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.getInputStream().transferTo(System.out);
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0)
                throw new RuntimeException("FFmpeg failed for " + resolution + " with exit code " + exitCode);
        } catch (InterruptedException e) {
            throw new RuntimeException("FFmpeg interrupted for resolution " + resolution);
        }
    }

    private void generateMasterPlaylist(String outputDir, List<String> resolutions) throws IOException {
        Path masterPlaylist = Paths.get(outputDir, "master.m3u8");
        try (BufferedWriter writer = Files.newBufferedWriter(masterPlaylist)) {
            writer.write("#EXTM3U\n");
            for (String res : resolutions) {
                String bandwidth = resolutionBitrates.get(res).replace("k", "000"); // e.g., 500k -> 500000
                writer.write("#EXT-X-STREAM-INF:BANDWIDTH=" + bandwidth + ",RESOLUTION=" + resolutionParams.get(res) + "\n");
                writer.write(res + "/stream.m3u8\n");
            }
        }
    }
}
