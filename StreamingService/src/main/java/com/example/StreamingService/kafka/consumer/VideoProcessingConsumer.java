package com.example.StreamingService.kafka.consumer;



import com.example.StreamingService.kafka.dto.VideoProcessingRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class VideoProcessingConsumer {

    @KafkaListener(topics = "video-processing", groupId = "video-streaming-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(VideoProcessingRequest request) {
        System.out.println("Received from Kafka: " + request);
    }
}
