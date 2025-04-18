package com.example.StreamingService.kafka.consumer;

import com.example.StreamingService.kafka.dto.VideoProcessingRequest;
import com.example.StreamingService.service.impl.VideoTranscodingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class VideoProcessingConsumer {

    private final VideoTranscodingService transcodingService;

    public VideoProcessingConsumer(VideoTranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    @KafkaListener(topics = "video-processing", groupId = "video-streaming-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(VideoProcessingRequest request) {
        try {
            System.out.println("Received request to process video: " + request.getVideoId());
            // Call transcoding service to process the video and create HLS
            transcodingService.transcodeToHLS(request);
        } catch (Exception e) {
            System.err.println("Error processing video " + request.getVideoId() + ": " + e.getMessage());
        }
    }
}
