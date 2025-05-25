package org.example.mikhaylovivan2semesterpart2.audit;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.config.TestConfig;
import org.example.mikhaylovivan2semesterpart2.config.TestKafkaConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestKafkaConfig.class, TestConfig.class})
class AuditControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    TestConfig.clearAuditMessages();
  }

  @AfterEach
  void tearDown() {
    TestConfig.clearAuditMessages();
  }

  @Test
  void testCreateAudit() {
    AuditMessage message = new AuditMessage();
    message.setUserId("test-user");
    message.setAction("CREATE");
    message.setEntityType("TEST");
    message.setEntityId("123");
    message.setTimestamp(LocalDateTime.now());
    message.setDetails("Test details");

    webTestClient.post()
        .uri("/api/audit")
        .header("X-User-Id", "test-user")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(message)
        .exchange()
        .expectStatus().isOk();

    TestConfig.addAuditMessage(message);
  }

  @Test
  void testCreateAuditWithoutUserId() {
    AuditMessage message = new AuditMessage();
    message.setAction("CREATE");
    message.setEntityType("TEST");
    message.setEntityId("123");
    message.setTimestamp(LocalDateTime.now());
    message.setDetails("Test details");

    webTestClient.post()
        .uri("/api/audit")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(message)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.message").isEqualTo("userId is required");
  }

  @Test
  void testGetAuditLogs() {
    AuditMessage message = new AuditMessage();
    message.setUserId("test-user");
    message.setAction("CREATE");
    message.setEntityType("TEST");
    message.setEntityId("123");
    message.setTimestamp(LocalDateTime.now());
    message.setDetails("Test details");

    TestConfig.addAuditMessage(message);

    webTestClient.get()
        .uri("/api/audit")
        .header("X-User-Id", "test-user")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(AuditMessage.class)
        .hasSize(1);
  }

  @Test
  void testGetAuditLogsByEntityId() {
    AuditMessage message = new AuditMessage();
    message.setUserId("test-user");
    message.setAction("CREATE");
    message.setEntityType("TEST");
    message.setEntityId("123");
    message.setTimestamp(LocalDateTime.now());
    message.setDetails("Test details");

    TestConfig.addAuditMessage(message);

    webTestClient.get()
        .uri("/api/audit/entity/123")
        .header("X-User-Id", "test-user")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(AuditMessage.class)
        .hasSize(1);
  }
}
