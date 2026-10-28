package org.aditya.postmorterm.bundle;

/** The injected cause. Stored beside the bundle but never exposed to agents. */
public record GroundTruth(String faultType, String tier, String service, String summary) {
}
