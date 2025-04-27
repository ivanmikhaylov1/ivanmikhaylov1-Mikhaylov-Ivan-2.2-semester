package org.example.mikhaylovivan2semesterpart2.audit.repository;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserAuditRepository {
  Mono<AuditMessage> save(AuditMessage message);

  Flux<AuditMessage> findAll();

  Flux<AuditMessage> findByEntityId(String entityId);
}
