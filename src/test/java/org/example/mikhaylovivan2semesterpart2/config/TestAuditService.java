package org.example.mikhaylovivan2semesterpart2.config;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.audit.service.KafkaAuditService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Primary
public class TestAuditService extends KafkaAuditService {
  public TestAuditService() {
    super(null);
  }

  @Override
  public Mono<Void> sendAudit(AuditMessage auditMessage) {
    TestConfig.addAuditMessage(auditMessage);
    return Mono.empty();
  }
}
