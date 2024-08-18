package com.ws.backend.repository;

import com.ws.backend.entity.SystemMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SystemMetricsRepository extends JpaRepository<SystemMetricsEntity, UUID> {
}
