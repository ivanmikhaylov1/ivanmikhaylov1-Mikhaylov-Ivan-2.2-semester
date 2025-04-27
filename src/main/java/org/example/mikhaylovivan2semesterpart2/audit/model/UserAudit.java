package org.example.mikhaylovivan2semesterpart2.audit.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
public class UserAudit {
  private UUID id;
  private String userId;
  private String action;
  private String details;
  private Instant timestamp;

  public UserAudit() {
  }

  public UserAudit(UUID id, String userId, String action, String details, Instant timestamp) {
    this.id = id;
    this.userId = userId;
    this.action = action;
    this.details = details;
    this.timestamp = timestamp;
  }
}
