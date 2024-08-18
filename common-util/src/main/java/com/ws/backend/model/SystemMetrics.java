package com.ws.backend.model;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SystemMetrics {
    private LocalDateTime time;
    private double cpuUsage;
    private double totalMemorySize;
    private double freeMemorySize;
    private double memoryUsage;
}