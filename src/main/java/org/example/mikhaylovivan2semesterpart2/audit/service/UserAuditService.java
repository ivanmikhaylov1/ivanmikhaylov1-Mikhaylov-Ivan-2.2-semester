package org.example.mikhaylovivan2semesterpart2.audit.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.example.mikhaylovivan2semesterpart2.audit.model.UserAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserAuditService {
  private final CqlSession session;
  private final PreparedStatement insertStatement;
  private final PreparedStatement selectStatement;

  @Autowired
  public UserAuditService(CqlSession session) {
    this.session = session;

    this.insertStatement = session.prepare(
        "INSERT INTO user_audit (id, user_id, action, details, timestamp) VALUES (?, ?, ?, ?, ?) USING TTL 31536000"
    );

    this.selectStatement = session.prepare(
        "SELECT id, user_id, action, details, timestamp FROM user_audit WHERE user_id = ?"
    );
  }

  public void createAudit(String userId, String action, String details) {
    BoundStatement boundStatement = insertStatement.bind()
        .setUuid(0, UUID.randomUUID())
        .setString(1, userId)
        .setString(2, action)
        .setString(3, details)
        .setInstant(4, Instant.now());

    session.execute(boundStatement);
  }

  public List<UserAudit> getAuditsByUserId(String userId) {
    BoundStatement boundStatement = selectStatement.bind()
        .setString(0, userId);

    ResultSet resultSet = session.execute(boundStatement);
    List<UserAudit> audits = new ArrayList<>();

    for (Row row : resultSet) {
      UserAudit audit = new UserAudit();
      audit.setId(row.getUuid("id"));
      audit.setUserId(row.getString("user_id"));
      audit.setAction(row.getString("action"));
      audit.setDetails(row.getString("details"));
      audit.setTimestamp(row.getInstant("timestamp"));
      audits.add(audit);
    }

    return audits;
  }
}
