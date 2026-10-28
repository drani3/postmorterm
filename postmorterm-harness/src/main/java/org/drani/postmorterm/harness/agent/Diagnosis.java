package org.drani.postmorterm.harness.agent;

/**
 * An agent's answer. {@code faultType} names an entry of the fault catalog, or is null when the agent only
 * localises the faulty service.
 */
public record Diagnosis(String service, String faultType, String rationale) {
}
