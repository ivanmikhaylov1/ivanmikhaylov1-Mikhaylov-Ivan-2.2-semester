package org.example.mikhaylovivan2semesterpart2.audit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditMessage {
  private String userId;
  private String action;
  private String entityType;
  private String entityId;
  private LocalDateTime timestamp;
  private String details;
}
