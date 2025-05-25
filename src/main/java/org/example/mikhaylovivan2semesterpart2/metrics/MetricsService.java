package org.example.mikhaylovivan2semesterpart2.metrics;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Service
public class MetricsService {

  private final Timer auditServiceTimer;
  private final DistributionSummary auditServiceSummary;
  private final MeterRegistry meterRegistry;

  public MetricsService(Timer auditServiceTimer,
                        DistributionSummary auditServiceSummary,
                        MeterRegistry meterRegistry) {
    this.auditServiceTimer = auditServiceTimer;
    this.auditServiceSummary = auditServiceSummary;
    this.meterRegistry = meterRegistry;
  }

  public <T> T recordTimedOperation(Callable<T> callable) throws Exception {
    return auditServiceTimer.recordCallable(callable);
  }

  public <T> T recordTimedOperationSupplier(Supplier<T> supplier) {
    return auditServiceTimer.record(supplier);
  }

  public void recordValue(double value) {
    auditServiceSummary.record(value);
  }
}
