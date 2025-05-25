package org.example.mikhaylovivan2semesterpart2.audit.repository;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserAuditRepositoryImpl implements UserAuditRepository {

  private final ConcurrentHashMap<String, List<AuditMessage>> auditStore = new ConcurrentHashMap<>();

  @Override
  public Flux<AuditMessage> findAll() {
    return Flux.fromIterable(auditStore.values())
        .flatMap(Flux::fromIterable);
  }

  @Override
  public Flux<AuditMessage> findByEntityId(String entityId) {
    return Mono.justOrEmpty(auditStore.get(entityId))
        .flatMapMany(Flux::fromIterable);
  }

  public Mono<AuditMessage> save(AuditMessage message) {
    return Mono.fromRunnable(() -> auditStore.computeIfAbsent(message.getEntityId(), k -> new ArrayList<>())
        .add(message));
  }
}
