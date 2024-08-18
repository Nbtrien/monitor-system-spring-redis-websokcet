package com.ws.backend.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.management.OperatingSystemMXBean;
import com.ws.backend.model.SystemMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisStreamProducer {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis.stream.metrics.key}")
    private String streamKey;

    @Scheduled(fixedRate = 1000)
    public void produceMetrics() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        double cpuUsage = osBean.getCpuLoad() * 100;
        long totalMemorySize = osBean.getTotalMemorySize();
        long freeMemorySize = osBean.getFreeMemorySize();
        double totalMemorySizeMB = totalMemorySize / (1024.0 * 1024.0);
        double freeMemorySizeMB = freeMemorySize / (1024.0 * 1024.0);
        double memoryUsagePercent = 100 - (freeMemorySize * 100.0 / totalMemorySize);

        SystemMetrics metrics = new SystemMetrics(LocalDateTime.now(), cpuUsage, totalMemorySizeMB, freeMemorySizeMB,
                                                  memoryUsagePercent);
        log.info(metrics);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            String metricsJson = objectMapper.writeValueAsString(metrics);
            redisTemplate.opsForStream().add(StreamRecords.newRecord().ofObject(metricsJson).withStreamKey(streamKey));
        } catch (JsonProcessingException e) {
            log.error("Error serializing metrics object", e);
        }
    }
}
