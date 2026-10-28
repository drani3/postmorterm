package org.drani.postmorterm.faults;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Component;

/** Faults currently active in this service. Service code checks it at the points where each fault applies. */
@Component
public class FaultSwitchboard {

    private final Set<FaultType> active = EnumSet.noneOf(FaultType.class);

    public synchronized boolean isActive(FaultType fault) {
        return active.contains(fault);
    }

    public synchronized void activate(FaultType fault) {
        active.add(fault);
    }

    public synchronized void clear(FaultType fault) {
        active.remove(fault);
    }

    public synchronized Set<FaultType> active() {
        Set<FaultType> copy = EnumSet.noneOf(FaultType.class);
        copy.addAll(active);
        return copy;
    }
}
