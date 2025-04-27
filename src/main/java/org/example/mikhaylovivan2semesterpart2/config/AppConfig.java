package org.example.mikhaylovivan2semesterpart2.config;

import org.example.mikhaylovivan2semesterpart2.audit.repository.UserAuditRepository;
import org.example.mikhaylovivan2semesterpart2.audit.repository.UserAuditRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public UserAuditRepository userAuditRepository() {
    return new UserAuditRepositoryImpl();
  }
}
