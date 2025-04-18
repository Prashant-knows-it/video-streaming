package com.example.StreamingService.service.impl;

import com.example.StreamingService.kafka.dto.VideoProcessingRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class VideoTranscodingService {

    private static final String FFmpeg_PATH = "C:\\ffmpeg\\bin\\ffmpeg.exe"; // Path to your ffmpeg executable

    public void transcodeToHLS(VideoProcessingRequest request) throws IOException {
        String originalVideoPath = request.getOriginalPath();
        String hlsOutputDir = request.getHlsOutputDir(); // already includes videoId
        String videoId = request.getVideoId();

        // Create resolution directories
        String[] resolutions = {"360p", "480p", "720p"};
        Map<String, String> resolutionParams = Map.of(
                "360p", "640:360",
                "480p", "854:480",
                "720p", "1280:720"
        );

        for (String res : resolutions) {
            Path resDir = Paths.get(hlsOutputDir, res);
            Files.createDirectories(resDir);

            String outStream = resDir.resolve("stream.m3u8").toString();
            String segmentPattern = resDir.resolve("segment%03d.ts").toString();

            List<String> command = Arrays.asList(
                    "cmd.exe", "/c", FFmpeg_PATH,
                    "-i", originalVideoPath,
                    "-vf", "scale=" + resolutionParams.get(res),
                    "-c:v", "libx264", "-preset", "fast", "-b:v", "500k",
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
            process.getInputStream().transferTo(System.out); // Print FFmpeg output
            int exitCode;
            try {
                exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("FFmpeg failed with code " + exitCode + " for resolution " + res);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("FFmpeg interrupted for resolution " + res);
            }
        }

        System.out.println("HLS transcoding completed for video: " + videoId);
    }

    private void transcodeToResolution(String originalVideoPath, String hlsOutputDir, String videoId, String resolution, String bitrate) throws IOException {
        // Correct folder structure: create resolution folder directly under videoId
        Path resolutionPath = Paths.get(hlsOutputDir, videoId, resolution);
        resolutionPath.toFile().mkdirs(); // Create resolution folder if not exists

        // FFmpeg command for transcoding
        String ffmpegCommand = String.format(
                "%s -i %s -vf scale=%s -c:v libx264 -preset fast -b:v %sk -c:a aac -b:a 128k -f hls " +
                        "-hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment%%03d.ts\" %s/stream.m3u8",
                FFmpeg_PATH, originalVideoPath, getResolution(resolution), bitrate, resolutionPath.toString(), resolutionPath.toString()
        );

        System.out.println("Running FFmpeg command for " + resolution + ": " + ffmpegCommand);

        // Execute FFmpeg command
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand.split(" "));
        Process process = processBuilder.start();

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg transcoding failed for " + resolution + " with exit code: " + exitCode);
            }
            System.out.println("Transcoding to HLS " + resolution + " completed for video: " + videoId);
        } catch (InterruptedException e) {
            throw new IOException("FFmpeg process interrupted for " + resolution, e);
        }
    }

    // Function to return resolution specific scale
    private String getResolution(String resolution) {
        switch (resolution) {
            case "360p": return "640:360";
            case "480p": return "854:480";
            case "720p": return "1280:720";
            case "1080p": return "1920:1080";
            default: return "640:360"; // Default to 360p if resolution is invalid
        }
    }
}
