package org.aditya.postmorterm.faults;

/** Difficulty tier of a fault, roughly how far its symptoms land from its cause. */
public enum Tier {
    /** Outright outage: errors in the faulty service itself. */
    T1_LOUD,
    /** Degradation: latency, retries or partial failures near the cause. */
    T2_DEGRADED,
    /** Silent misbehaviour: symptoms surface in a different service than the cause. */
    T3_DISTANT,
    /** Misleading: a red herring, such as an unrelated deploy, coincides with the real cause. */
    T4_MISLEADING
}
