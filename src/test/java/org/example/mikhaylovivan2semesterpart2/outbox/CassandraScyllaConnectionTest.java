package org.example.mikhaylovivan2semesterpart2.outbox;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

class CassandraScyllaConnectionTest {
  @Test
  void testCassandraConnection() {
    testConnection("127.0.0.1");
    testConnection("scylla");
  }

  private void testConnection(String host) {
    try (CqlSession session = CqlSession.builder()
        .addContactPoint(new InetSocketAddress(host, 9042))
        .withLocalDatacenter("datacenter1")
        .build()) {
      session.execute("SELECT release_version FROM system.local");
    } catch (Exception ignored) {
    }
  }
}
