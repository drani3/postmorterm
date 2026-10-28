package org.aditya.postmorterm.bundle;

import java.time.Instant;
import java.util.List;

/** What the agent is told up front: the alert that fired and the incident window. */
public record BundleManifest(String incidentId, String alert, Instant windowStart, Instant windowEnd,
                             List<String> services) {
}
