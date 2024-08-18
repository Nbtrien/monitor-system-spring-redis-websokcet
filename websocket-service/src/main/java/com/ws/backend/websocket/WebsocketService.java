package com.ws.backend.websocket;

import com.ws.backend.model.SystemMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class WebsocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendToWebSocket(SystemMetrics metrics) {
        SystemMetricsResponseDto responseDto = new SystemMetricsResponseDto(metrics.getTime(), metrics.getCpuUsage(),
                                                                            metrics.getTotalMemorySize(),
                                                                            metrics.getFreeMemorySize(),
                                                                            metrics.getMemoryUsage());
        log.info(responseDto);
        messagingTemplate.convertAndSend("/topic/metrics", responseDto);
    }
}
