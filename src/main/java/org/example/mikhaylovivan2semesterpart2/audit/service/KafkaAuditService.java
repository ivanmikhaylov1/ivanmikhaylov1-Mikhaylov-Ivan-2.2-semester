package org.example.mikhaylovivan2semesterpart2.audit.service;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.config.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KafkaAuditService {

  private final KafkaTemplate<String, AuditMessage> kafkaTemplate;

  public KafkaAuditService(KafkaTemplate<String, AuditMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public Mono<Void> sendAudit(AuditMessage auditMessage) {
    if (kafkaTemplate == null) {
      return Mono.empty();
    }
    return Mono.fromFuture(kafkaTemplate.send(KafkaConfig.AUDIT_TOPIC, auditMessage))
        .then();
  }
}
