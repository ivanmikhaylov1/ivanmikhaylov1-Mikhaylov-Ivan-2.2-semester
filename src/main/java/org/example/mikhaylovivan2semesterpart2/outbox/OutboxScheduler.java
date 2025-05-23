package org.example.mikhaylovivan2semesterpart2.outbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxScheduler {
  private final OutboxService outboxService;

  @Autowired
  public OutboxScheduler(OutboxService outboxService) {
    this.outboxService = outboxService;
  }

  @Scheduled(fixedDelayString = "${outbox.poll.delay:1000}")
  public void processOutbox() {
    outboxService.processOutboxMessages();
  }
}
