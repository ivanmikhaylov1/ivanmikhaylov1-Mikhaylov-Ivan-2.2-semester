package org.example.mikhaylovivan2semesterpart2.outbox;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxIntegrationTest {

  @Mock
  private CqlSession session;

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  @Mock
  private ResultSet resultSet;

  @Mock
  private Row row;

  private OutboxService outboxService;

  @BeforeEach
  void setUp() {
    outboxService = new OutboxService(session, kafkaTemplate);
    when(session.execute("SELECT * FROM user_audit.outbox")).thenReturn(resultSet);
  }

  @Test
  void testSuccessfulMessageDelivery() {
    String message = "test message";
    UUID messageId = UUID.randomUUID();

    when(row.getUuid("id")).thenReturn(messageId);
    when(row.getString("value")).thenReturn(message);
    when(resultSet.all()).thenReturn(Collections.singletonList(row));
    when(kafkaTemplate.send(anyString(), eq(message))).thenReturn(null);

    outboxService.processOutboxMessages();

    verify(kafkaTemplate).send(anyString(), eq(message));
    verify(session).execute(eq("DELETE FROM user_audit.outbox WHERE id = ?"), eq(messageId));
  }

  @Test
  void testMessagePersistenceOnKafkaFailure() {
    String message = "test message on failure";
    UUID messageId = UUID.randomUUID();

    when(row.getUuid("id")).thenReturn(messageId);
    when(row.getString("value")).thenReturn(message);
    when(resultSet.all()).thenReturn(Collections.singletonList(row));
    when(kafkaTemplate.send(anyString(), eq(message))).thenThrow(new RuntimeException("Kafka недоступна"));

    outboxService.processOutboxMessages();

    verify(kafkaTemplate).send(anyString(), eq(message));
    verify(session, never()).execute(eq("DELETE FROM user_audit.outbox WHERE id = ?"), eq(messageId));
  }

  @Test
  void testAtLeastOnceDelivery() {
    String message = "at-least-once message";
    UUID messageId = UUID.randomUUID();

    when(row.getUuid("id")).thenReturn(messageId);
    when(row.getString("value")).thenReturn(message);
    when(resultSet.all()).thenReturn(
        Collections.singletonList(row),
        Collections.singletonList(row)
    );


    when(kafkaTemplate.send(anyString(), eq(message)))
        .thenThrow(new RuntimeException("Kafka недоступна первый раз"))
        .thenReturn(null);

    outboxService.processOutboxMessages();

    verify(session, never()).execute(eq("DELETE FROM user_audit.outbox WHERE id = ?"), eq(messageId));

    outboxService.processOutboxMessages();

    verify(session).execute(eq("DELETE FROM user_audit.outbox WHERE id = ?"), eq(messageId));
  }

  @Test
  void testBatchMessageProcessing() {
    int messageCount = 3;
    List<Row> rows = new ArrayList<>();
    List<UUID> messageIds = new ArrayList<>();

    for (int i = 0; i < messageCount; i++) {
      UUID messageId = UUID.randomUUID();
      String message = "batch message " + i;
      messageIds.add(messageId);

      Row mockRow = mock(Row.class);
      when(mockRow.getUuid("id")).thenReturn(messageId);
      when(mockRow.getString("value")).thenReturn(message);
      rows.add(mockRow);
    }

    when(resultSet.all()).thenReturn(rows);

    for (int i = 0; i < messageCount; i++) {
      when(kafkaTemplate.send(anyString(), eq("batch message " + i))).thenReturn(null);
    }

    outboxService.processOutboxMessages();

    for (int i = 0; i < messageCount; i++) {
      verify(kafkaTemplate).send(anyString(), eq("batch message " + i));
      verify(session).execute(eq("DELETE FROM user_audit.outbox WHERE id = ?"), eq(messageIds.get(i)));
    }
  }
}
