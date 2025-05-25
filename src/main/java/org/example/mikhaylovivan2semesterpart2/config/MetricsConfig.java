package org.example.mikhaylovivan2semesterpart2.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MetricsConfig {

  @Bean
  public Timer auditServiceTimer(MeterRegistry registry) {
    return Timer.builder("audit.service.timer")
        .description("Метрика времени выполнения операций в сервисе аудита")
        .publishPercentileHistogram()
        .publishPercentiles(0.5, 0.75, 0.95, 0.99)
        .serviceLevelObjectives(
            Duration.ofMillis(50),
            Duration.ofMillis(100),
            Duration.ofMillis(200))
        .register(registry);
  }

  @Bean
  public io.micrometer.core.instrument.DistributionSummary auditServiceSummary(MeterRegistry registry) {
    return io.micrometer.core.instrument.DistributionSummary.builder("audit.service.summary")
        .description("Метрика распределения размеров сообщений аудита")
        .publishPercentileHistogram()
        .publishPercentiles(0.5, 0.75, 0.95, 0.99)
        .serviceLevelObjectives(10, 50, 100, 500, 1000)
        .register(registry);
  }
}
