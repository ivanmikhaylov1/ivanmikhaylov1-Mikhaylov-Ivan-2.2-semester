package org.example.mikhaylovivan2semesterpart2.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class ScyllaConfig {

  @Bean
  public CqlSession cqlSession() {
    CqlSession session = CqlSession.builder()
        .addContactPoint(new java.net.InetSocketAddress("localhost", 9042))
        .withLocalDatacenter("datacenter1")
        .build();
    
    initializeSchema(session);
    return session;
  }

  private void initializeSchema(CqlSession session) {
    session.execute("CREATE KEYSPACE IF NOT EXISTS user_audit " +
        "WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}");

    session.execute("USE user_audit");
    session.execute("CREATE TABLE IF NOT EXISTS user_audit (" +
        "id UUID PRIMARY KEY," +
        "user_id TEXT," +
        "action TEXT," +
        "details TEXT," +
        "timestamp TIMESTAMP" +
        ") WITH default_time_to_live = 31536000");

    session.execute("CREATE INDEX IF NOT EXISTS ON user_audit (user_id)");
  }
}
