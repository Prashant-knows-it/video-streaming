package com.example.VideoService.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class VideoUtils {

    public static String extractThumbnail(Path videoPath, Path thumbnailPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "C:/ffmpeg/bin/ffmpeg",
                    "-i", videoPath.toString(),
                    "-ss", "00:00:01",
                    "-vframes", "1",
                    thumbnailPath.toString()
            );
            Process process = builder.start();
            process.waitFor(10, TimeUnit.SECONDS);
            return thumbnailPath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract thumbnail", e);
        }
    }

    public static long extractDuration(Path videoPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "C:/ffmpeg/bin/ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    videoPath.toString()
            );
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor(5, TimeUnit.SECONDS);
            return (long) Double.parseDouble(line);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract duration", e);
        }
    }
}