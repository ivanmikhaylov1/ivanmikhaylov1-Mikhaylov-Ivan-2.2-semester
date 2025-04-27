package org.example.mikhaylovivan2semesterpart2.audit.controller;

import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.audit.service.AuditService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
  private final AuditService auditService;

  public AuditController(AuditService auditService) {
    this.auditService = auditService;
  }

  @PostMapping
  public Mono<AuditMessage> createAudit(@RequestBody AuditMessage auditMessage) {
    if (auditMessage.getUserId() == null || auditMessage.getUserId().trim().isEmpty()) {
      return Mono.error(new IllegalArgumentException("userId is required"));
    }
    return auditService.createAudit(auditMessage);
  }

  @GetMapping
  public Flux<AuditMessage> getAuditLogs() {
    return auditService.getAllAuditLogs();
  }

  @GetMapping("/entity/{entityId}")
  public Flux<AuditMessage> getAuditLogsByEntityId(@PathVariable String entityId) {
    return auditService.getAuditLogsByEntityId(entityId);
  }
}
