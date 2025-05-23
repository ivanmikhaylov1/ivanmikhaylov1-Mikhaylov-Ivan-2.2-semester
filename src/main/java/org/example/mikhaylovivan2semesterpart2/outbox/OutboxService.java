package org.example.mikhaylovivan2semesterpart2.outbox;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OutboxService {
  private final CqlSession session;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  public OutboxService(CqlSession session, KafkaTemplate<String, String> kafkaTemplate) {
    this.session = session;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void processOutboxMessages() {
    var result = session.execute("SELECT * FROM user_audit.outbox");
    List<Row> rows = result.all();

    for (Row row : rows) {
      UUID id = row.getUuid("id");
      String value = row.getString("value");

      try {
        String topicName = "user-audit";
        kafkaTemplate.send(topicName, value);

        session.execute(
            "DELETE FROM user_audit.outbox WHERE id = ?",
            id
        );
      } catch (Exception ignored) {
      }
    }
  }
}
