package org.example.mikhaylovivan2semesterpart2.outbox;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Setter
@Getter
@Table("outbox")
public class OutboxEvent {
  @PrimaryKey
  private UUID id;
  private String value;

  public OutboxEvent() {
  }
}
