package org.example.mikhaylovivan2semesterpart2.audit.service;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.audit.repository.UserAuditRepository;
import org.example.mikhaylovivan2semesterpart2.metrics.MetricsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class AuditService {
  private final UserAuditRepository userAuditRepository;
  private final MetricsService metricsService;

  public AuditService(UserAuditRepository userAuditRepository, MetricsService metricsService) {
    this.userAuditRepository = userAuditRepository;
    this.metricsService = metricsService;
  }

  public Mono<AuditMessage> createAudit(AuditMessage auditMessage) {
    if (auditMessage.getUserId() == null || auditMessage.getUserId().trim().isEmpty()) {
      return Mono.error(new IllegalArgumentException("userId is required"));
    }

    int msgSize = calculateMessageSize(auditMessage);
    metricsService.recordValue(msgSize);

    auditMessage.setTimestamp(LocalDateTime.now());

    return Mono.fromCallable(() -> {
      try {
        return metricsService.recordTimedOperation(() -> userAuditRepository.save(auditMessage).block());
      } catch (Exception e) {
        throw new RuntimeException("Ошибка при сохранении сообщения аудита", e);
      }
    });
  }

  public Flux<AuditMessage> getAllAuditLogs() {
    return Flux.defer(() -> {
      try {
        return metricsService.recordTimedOperationSupplier(userAuditRepository::findAll);
      } catch (Exception e) {
        return Flux.error(new RuntimeException("Ошибка при получении всех логов аудита", e));
      }
    });
  }

  public Flux<AuditMessage> getAuditLogsByEntityId(String entityId) {
    return Flux.defer(() -> {
      try {
        return metricsService.recordTimedOperationSupplier(() -> userAuditRepository.findByEntityId(entityId));
      } catch (Exception e) {
        return Flux.error(new RuntimeException("Ошибка при получении логов аудита по entityId: " + entityId, e));
      }
    });
  }

  private int calculateMessageSize(AuditMessage message) {
    int size = 0;
    if (message.getUserId() != null) size += message.getUserId().getBytes(StandardCharsets.UTF_8).length;
    if (message.getAction() != null) size += message.getAction().getBytes(StandardCharsets.UTF_8).length;
    if (message.getEntityType() != null) size += message.getEntityType().getBytes(StandardCharsets.UTF_8).length;
    if (message.getEntityId() != null) size += message.getEntityId().getBytes(StandardCharsets.UTF_8).length;
    if (message.getDetails() != null) size += message.getDetails().getBytes(StandardCharsets.UTF_8).length;
    return size;
  }
}
