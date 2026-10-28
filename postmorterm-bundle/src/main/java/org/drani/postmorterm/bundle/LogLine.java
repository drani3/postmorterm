package org.drani.postmorterm.bundle;

import java.time.Instant;

public record LogLine(Instant ts, String service, String level, String traceId, String message) {
}
