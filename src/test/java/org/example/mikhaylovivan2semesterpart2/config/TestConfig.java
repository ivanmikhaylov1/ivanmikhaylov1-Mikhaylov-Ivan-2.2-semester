package org.example.mikhaylovivan2semesterpart2.config;

import lombok.Getter;
import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.example.mikhaylovivan2semesterpart2.audit.repository.UserAuditRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

  @Bean
  @Primary
  public UserAuditRepository testUserAuditRepository() {
    return new UserAuditRepository() {
      @Override
      public Mono<AuditMessage> save(AuditMessage message) {
        auditMessages.add(message);
        return Mono.just(message);
      }

      @Override
      public Flux<AuditMessage> findAll() {
        return Flux.fromIterable(auditMessages);
      }

      @Override
      public Flux<AuditMessage> findByEntityId(String entityId) {
        return Flux.fromIterable(auditMessages)
            .filter(message -> message.getEntityId().equals(entityId));
      }
    };
  }
}
