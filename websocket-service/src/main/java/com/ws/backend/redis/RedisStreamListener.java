package com.ws.backend.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ws.backend.model.SystemMetrics;
import com.ws.backend.service.SystemMetricsService;
import com.ws.backend.websocket.WebsocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RedisStreamListener implements StreamListener<String, ObjectRecord<String, String>> {
    private final SystemMetricsService systemMetricsService;
    private final WebsocketService websocketService;

    @Override
    public void onMessage(ObjectRecord<String, String> record) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String metricsJson = record.getValue();
        try {
            SystemMetrics metrics = objectMapper.readValue(metricsJson, SystemMetrics.class);
            CompletableFuture.runAsync(() -> systemMetricsService.saveMetrics(metrics));
            CompletableFuture.runAsync(() -> websocketService.sendToWebSocket(metrics));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
