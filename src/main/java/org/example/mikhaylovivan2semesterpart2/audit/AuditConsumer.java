package org.example.mikhaylovivan2semesterpart2.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.audit.repository.UserAuditRepository;
import org.example.mikhaylovivan2semesterpart2.config.KafkaConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

  private final UserAuditRepository userAuditRepository;

  @KafkaListener(topics = KafkaConfig.AUDIT_TOPIC, groupId = "audit-group")
  public void consume(AuditMessage message) {
    log.info("Received audit message: {}", message);
    userAuditRepository.save(message).subscribe(
        savedMessage -> log.info("Audit message saved: {}", savedMessage),
        error -> log.error("Error saving audit message", error)
    );
  }
}
