package org.example.mikhaylovivan2semesterpart2.audit.service;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.audit.repository.UserAuditRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class AuditService {
  private final UserAuditRepository userAuditRepository;

  public AuditService(UserAuditRepository userAuditRepository) {
    this.userAuditRepository = userAuditRepository;
  }

  public Mono<AuditMessage> createAudit(AuditMessage auditMessage) {
    if (auditMessage.getUserId() == null || auditMessage.getUserId().trim().isEmpty()) {
      return Mono.error(new IllegalArgumentException("userId is required"));
    }
    auditMessage.setTimestamp(LocalDateTime.now());
    return userAuditRepository.save(auditMessage);
  }

  public Flux<AuditMessage> getAllAuditLogs() {
    return userAuditRepository.findAll();
  }

  public Flux<AuditMessage> getAuditLogsByEntityId(String entityId) {
    return userAuditRepository.findByEntityId(entityId);
  }
}
