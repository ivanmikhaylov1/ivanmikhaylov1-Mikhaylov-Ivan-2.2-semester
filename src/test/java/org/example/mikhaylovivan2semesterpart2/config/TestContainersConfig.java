package org.example.mikhaylovivan2semesterpart2.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.mikhaylovivan2semesterpart2.audit.model.AuditMessage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@Testcontainers
@Profile("test")
public class TestContainersConfig {

  @Container
  private static final CassandraContainer<?> cassandra =
      new CassandraContainer<>(DockerImageName.parse("cassandra:4.0"))
          .withExposedPorts(9042);

  @Container
  private static final KafkaContainer kafka =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
          .withExposedPorts(9092, 9093);

  static {
    cassandra.start();

    try {
      int maxRetries = 10;
      int retryIntervalMs = 1000;
      boolean connected = false;

      for (int i = 0; i < maxRetries; i++) {
        try (CqlSession session = CqlSession.builder()
            .addContactPoint(cassandra.getContactPoint())
            .withLocalDatacenter("datacenter1")
            .build()) {

          session.execute("CREATE KEYSPACE IF NOT EXISTS user_audit WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
          session.execute("CREATE TABLE IF NOT EXISTS user_audit.outbox (id uuid PRIMARY KEY, value text)");
          connected = true;
          System.out.println("Successfully connected to Cassandra and created schema");
          break;
        } catch (Exception e) {
          System.out.println("Failed to connect to Cassandra, retry " + (i + 1) + "/" + maxRetries);
          try {
            Thread.sleep(retryIntervalMs);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
          }
        }
      }

      if (!connected) {
        System.err.println("Failed to connect to Cassandra after " + maxRetries + " attempts");
      }
    } catch (Exception e) {
      System.err.println("Error setting up Cassandra: " + e.getMessage());
      e.printStackTrace();
    }

    kafka.start();

    System.setProperty("spring.data.cassandra.contact-points", cassandra.getHost());
    System.setProperty("spring.data.cassandra.port", String.valueOf(cassandra.getMappedPort(9042)));
    System.setProperty("spring.data.cassandra.local-datacenter", cassandra.getLocalDatacenter());
    System.setProperty("KAFKA_BOOTSTRAP_SERVERS", kafka.getBootstrapServers());
  }

  @Bean
  @Primary
  public CqlSession testCqlSession() {
    return CqlSession.builder()
        .addContactPoint(cassandra.getContactPoint())
        .withLocalDatacenter(cassandra.getLocalDatacenter())
        .withKeyspace("user_audit")
        .build();
  }

  @Bean
  @Primary
  public CassandraTemplate cassandraTemplate(CqlSession cqlSession) {
    return new CassandraTemplate(cqlSession);
  }

  @Bean
  @Primary
  public KafkaTemplate<String, String> kafkaTemplate() {
    Map<String, Object> producerProps = Map.of(
        "bootstrap.servers", kafka.getBootstrapServers(),
        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer"
    );
    ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  @Primary
  public KafkaTemplate<String, AuditMessage> auditKafkaTemplate() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    ProducerFactory<String, AuditMessage> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
    return new KafkaTemplate<>(producerFactory);
  }
}
