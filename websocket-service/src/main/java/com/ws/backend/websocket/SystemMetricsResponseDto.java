package com.ws.backend.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SystemMetricsResponseDto {
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime time;
    private String cpuUsage;
    private String totalMemorySize;
    private String freeMemorySize;
    private String memoryUsage;

    public SystemMetricsResponseDto(LocalDateTime time, double cpuUsage, double totalMemorySize, double freeMemorySize,
                                    double memoryUsage) {
        this.time = time;
        this.cpuUsage = formatNumber(cpuUsage) + "%";
        this.totalMemorySize = formatNumber(totalMemorySize) + " MB";
        this.freeMemorySize = formatNumber(freeMemorySize) + " MB";
        this.memoryUsage = formatNumber(memoryUsage) + "%";
    }

    private String formatNumber(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }
}
