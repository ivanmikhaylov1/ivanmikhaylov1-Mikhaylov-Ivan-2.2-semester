package org.example.mikhaylovivan2semesterpart2.outbox;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface OutboxEventRepository extends CassandraRepository<OutboxEvent, UUID> {
}
