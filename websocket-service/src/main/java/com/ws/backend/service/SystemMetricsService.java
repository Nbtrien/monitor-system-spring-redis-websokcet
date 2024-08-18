package com.ws.backend.service;

import com.ws.backend.entity.SystemMetricsEntity;
import com.ws.backend.model.SystemMetrics;
import com.ws.backend.repository.SystemMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
@RequiredArgsConstructor
public class SystemMetricsService {

    private final SystemMetricsRepository metricsRepository;

    public void saveMetrics(SystemMetrics metrics) {
        SystemMetricsEntity metricsEntity = SystemMetricsEntity.builder().time(metrics.getTime())
                .cpuUsage(metrics.getCpuUsage()).freeMemorySize(metrics.getFreeMemorySize())
                .totalMemorySize(metrics.getTotalMemorySize()).memoryUsagePercent(metrics.getMemoryUsage())
                .build();
        metricsRepository.save(metricsEntity);
    }
}