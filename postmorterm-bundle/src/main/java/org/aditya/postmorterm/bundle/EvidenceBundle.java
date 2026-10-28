package org.aditya.postmorterm.bundle;

import java.util.List;

/** Everything captured for one incident. Immutable so every agent replays identical evidence. */
public record EvidenceBundle(BundleManifest manifest, List<LogLine> logs, List<Span> spans,
                             List<MetricPoint> metrics, List<DeployEvent> deploys) {
}
