package com.ws.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "SYSTEM_METRICS")
@Entity
public class SystemMetricsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "AT_TIME")
    private LocalDateTime time;
    @Column(name = "CPU_USAGE")
    private double cpuUsage;
    @Column(name = "TOTAL_MEMORY_SIZE")
    private double totalMemorySize;
    @Column(name = "FREE_MEMORY_SIZE")
    private double freeMemorySize;
    @Column(name = "MEMORY_USAGE_PERCENT")
    private double memoryUsagePercent;
}
