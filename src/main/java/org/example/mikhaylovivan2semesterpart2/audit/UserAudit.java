package org.example.mikhaylovivan2semesterpart2.audit;

import java.time.Instant;
import java.util.UUID;

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

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }
}
