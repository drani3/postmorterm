package org.drani.postmorterm.bundle;

import java.time.Instant;

public record DeployEvent(Instant ts, String service, String version, String change) {
}
