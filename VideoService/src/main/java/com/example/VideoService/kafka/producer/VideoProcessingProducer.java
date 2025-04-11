package com.example.VideoService.kafka.producer;

import com.example.VideoService.kafka.dto.VideoProcessingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoProcessingProducer {

    private final KafkaTemplate<String, VideoProcessingRequest> kafkaTemplate;

    public void send(VideoProcessingRequest request) {
        kafkaTemplate.send("video-processing", request.getVideoId(), request);
    }
}
