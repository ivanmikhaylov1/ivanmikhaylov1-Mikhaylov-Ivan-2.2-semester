package org.example.mikhaylovivan2semesterpart2.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserAuditServiceIntegrationTest {

  @Autowired
  private UserAuditService userAuditService;

  @Test
  void testCreateAndGetAudit() {
    String userId = "test-user-1";
    String action = "LOGIN";
    String details = "User logged in successfully";

    userAuditService.createAudit(userId, action, details);
    List<UserAudit> audits = userAuditService.getAuditsByUserId(userId);

    assertNotNull(audits);
    assertFalse(audits.isEmpty());

    UserAudit audit = audits.get(0);
    assertEquals(userId, audit.getUserId());
    assertEquals(action, audit.getAction());
    assertEquals(details, audit.getDetails());
    assertNotNull(audit.getTimestamp());
    assertTrue(audit.getTimestamp().isBefore(Instant.now()));
  }

  @Test
  void testGetAuditsForNonExistentUser() {
    String nonExistentUserId = "non-existent-user";
    List<UserAudit> audits = userAuditService.getAuditsByUserId(nonExistentUserId);
    assertNotNull(audits);
    assertTrue(audits.isEmpty());
  }
}
