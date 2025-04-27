package org.example.mikhaylovivan2semesterpart2.audit;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.audit.repository.UserAuditRepository;
import org.example.mikhaylovivan2semesterpart2.audit.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class AuditServiceTest {

  @Autowired
  private AuditService auditService;

  @MockBean
  private UserAuditRepository auditRepository;

  @MockBean
  private KafkaTemplate<String, AuditMessage> kafkaTemplate;

  private AuditMessage testMessage;

  @BeforeEach
  void setUp() {
    testMessage = new AuditMessage();
    testMessage.setUserId("test-user");
    testMessage.setEntityId("test-entity");
    testMessage.setAction("CREATE");
    testMessage.setTimestamp(LocalDateTime.now());
    testMessage.setDetails("Test details");

    when(auditRepository.save(any(AuditMessage.class))).thenReturn(Mono.just(testMessage));
    when(auditRepository.findAll()).thenReturn(Flux.just(testMessage));
    when(auditRepository.findByEntityId("test-entity")).thenReturn(Flux.just(testMessage));
  }

  @Test
  void testCreateAudit() {
    StepVerifier.create(auditService.createAudit(testMessage))
        .expectNext(testMessage)
        .verifyComplete();
  }

  @Test
  void testCreateAuditWithoutUserId() {
    testMessage.setUserId(null);

    StepVerifier.create(auditService.createAudit(testMessage))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  void testGetAllAuditLogs() {
    StepVerifier.create(auditService.getAllAuditLogs())
        .expectNext(testMessage)
        .verifyComplete();
  }

  @Test
  void testGetAuditLogsByEntityId() {
    StepVerifier.create(auditService.getAuditLogsByEntityId("test-entity"))
        .expectNext(testMessage)
        .verifyComplete();
  }
}
