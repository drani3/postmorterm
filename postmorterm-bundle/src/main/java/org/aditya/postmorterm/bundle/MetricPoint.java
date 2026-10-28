package org.aditya.postmorterm.bundle;

import java.time.Instant;
import java.util.Map;

public record MetricPoint(Instant ts, String service, String name, double value, Map<String, String> labels) {
}
