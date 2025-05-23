package org.example.mikhaylovivan2semesterpart2.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPublisher {
  private static final String TOPIC = "events";
  private final OutboxEventRepository repository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public OutboxEventPublisher(
      OutboxEventRepository repository,
      KafkaTemplate<String, String> kafkaTemplate
  ) {
    this.repository = repository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Scheduled(fixedDelayString = "${outbox.poll.delay:1000}")
  public void pollAndPublish() {
    List<OutboxEvent> events = repository.findAll();
    for (OutboxEvent e : events) {
      try {
        kafkaTemplate.send(TOPIC, e.getValue()).get();
        repository.deleteById(e.getId());
      } catch (Exception ignored) {
      }
    }
  }
}
