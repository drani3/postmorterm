package org.drani.postmorterm.bundle;

import java.time.Instant;

public record Span(String traceId, String spanId, String parentSpanId, String service, String operation,
                   Instant start, long durationMs, String status) {
}
