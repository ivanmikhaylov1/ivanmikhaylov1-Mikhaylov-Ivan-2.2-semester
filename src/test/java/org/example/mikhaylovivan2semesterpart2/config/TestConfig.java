package org.example.mikhaylovivan2semesterpart2.config;

import lombok.Getter;
import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("test")
public class TestConfig {
  @Getter
  private static final List<AuditMessage> auditMessages = new ArrayList<>();

  public static void addAuditMessage(AuditMessage message) {
    auditMessages.add(message);
  }

  public static void clearAuditMessages() {
    auditMessages.clear();
  }
}
